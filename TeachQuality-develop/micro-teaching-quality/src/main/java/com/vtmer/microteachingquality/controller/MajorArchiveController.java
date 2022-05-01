package com.vtmer.microteachingquality.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.vtmer.microteachingquality.common.ResponseMessage;
import com.vtmer.microteachingquality.model.dto.result.*;
import com.vtmer.microteachingquality.model.entity.User;
import com.vtmer.microteachingquality.service.MajorArchiveService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;

/**
 * @author 墨小小
 */
@RestController
@RequestMapping("/archive")
@Api(tags = "专业归档与专业归档负责人接口列表")
@Slf4j
public class MajorArchiveController {

    /**
     * 随机生成存储加密文件名(课程自评报告)的密钥
     * 固定密钥，不然会出bug
     */
    private final byte[] ENCRYPT_KEY = "9fcccb8912be487e91dc2743d94e566a".getBytes();
    /**
     * 构建加密
     */
    private AES aes = SecureUtil.aes(ENCRYPT_KEY);

    @Value("${major.archive.file}")
    private String archiveFilePath;

    @Value("${major.archive.template}")
    private String templatePath;

    @Resource
    private MajorArchiveService majorArchiveService;

    /*
    todo
        专业归档：
            根据文件path删除文件 done
            上传文件（需要文件批次名） done
            获取所有文件（包括批次名，不同批次里的文件信息等） 可能要修改：获取批次后再获取文件，因为会出现有批次，没上传文件的情况 done
            根据文件path下载文件 done
            获取模板文件信息 done
            根据模板path下载模板 done
            获取负责人信息 (在user表根据userType获取) done
            增加新批次 (需要负责人id，批次名) done
        专业归档负责人：
            根据专业批次提交评审 done
            获取管理的专业与批次和评审状态 done
            根据专业批次获取文件列表 done
            根据专业和批次获取已经评审的信息opinion done
            上传模板 done
     */


    @ApiOperation("专业归档 获取专业归档负责人信息")
    @GetMapping("/getManagerInfo")
    public ResponseMessage<?> getManagerInfo() {
        return ResponseMessage.newSuccessInstance(majorArchiveService.getManagerInfo());
    }


    @ApiOperation("专业归档 添加新的归档批次")
    @PostMapping("/newBatch")
    @ApiImplicitParams({//用不了ApiParam就老老实实用这个
            @ApiImplicitParam(name = "managerId", value = "负责人id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "batchName", value = "批次名", required = true, paramType = "query")
    })
    public ResponseMessage<?> newBatch(Integer managerId, String batchName) {
        return ResponseMessage.newSuccessInstance(majorArchiveService.newBatch(managerId, batchName));
    }

    @ApiOperation(value = "专业归档 上传文件", notes = "batchName为评审批次名字")
    @PostMapping("/uploadFile")
    public ResponseMessage<?> uploadFile(@RequestPart MultipartFile file, String batchName) throws IOException {
        if (batchName == null || "".equals(batchName)) {
            return ResponseMessage.newErrorInstance("未选择批次");
        }
        // 获取当前登陆用户(课程负责人)对象
        User loginUser = JSON.parseObject(JSONUtil.toJsonStr(SecurityContextHolder.getContext().getAuthentication().getPrincipal()), User.class);
        if (ObjectUtil.isNull(file)) {
            return ResponseMessage.newErrorInstance("请选择需要上传的文件");
        } else if (StrUtil.isBlank(file.getOriginalFilename())) {
            return ResponseMessage.newErrorInstance("请选择需要上传的文件");
        }
        // 目录添加uuid防重
        String uuid = IdUtil.simpleUUID();
        File path = new File(archiveFilePath + File.separator + uuid);
        if (!path.isDirectory()) {
            path.mkdirs();
        }
        // 加密文件名
        String encryptFileName = aes.encryptHex(uuid + File.separator + file.getOriginalFilename());
        String filePath = archiveFilePath + File.separator + uuid + File.separator + file.getOriginalFilename();
        log.info("用户 :{}   上传归档文件 : {}    加密路径 :  {}", loginUser.getRealName(), file.getOriginalFilename(), encryptFileName);
        //进行信息存储
        if (majorArchiveService.uploadFileRecord(loginUser, batchName, file.getOriginalFilename(), encryptFileName) > 0) {
            FileUtil.writeBytes(file.getBytes(), filePath);
            log.info("用户 :{}   上传归档文件 : {}   上传成功", loginUser.getRealName(), file.getOriginalFilename());
            return ResponseMessage.newSuccessInstance("上传文件成功");
        } else {
            log.info("用户 :{}   上传归档文件 : {}   上传失败", loginUser.getRealName(), file.getOriginalFilename());
            return ResponseMessage.newErrorInstance("上传文件失败");
        }

    }

    @GetMapping("/downloadUploadedFile")
    @ApiOperation("专业归档 下载自己上传的文件")
    public void downloadFile(@Validated @ApiParam("加密路径") String path, HttpServletRequest request, HttpServletResponse response) {
        User loginUser = JSON.parseObject(JSONUtil.toJsonStr(SecurityContextHolder.getContext().getAuthentication().getPrincipal()), User.class);
        //解密路径
        String filePath = archiveFilePath + File.separator + aes.decryptStr(path, CharsetUtil.CHARSET_UTF_8);
        log.info("解密后文件路径: {}", filePath);
        // 截取文件名
        String fileName = StrUtil.subAfter(filePath, File.separator, true);
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            // 配置文件下载及避免呢中午呢乱码
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            // 输入流
            bis = new BufferedInputStream(new FileInputStream(filePath));
            // 输出流
            OutputStream os = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int temp = 0;
            // 每次读取的字符串长度
            while ((temp = bis.read(buffer)) != -1) {
                os.write(buffer, 0, temp);
            }
            //os正常写入了
            log.info("用户 {} 下载归档文件成功: {}", loginUser.getRealName(), fileName);
        } catch (Exception e) {
            log.error("用户  {}  下载归档文件失败: {}", loginUser.getRealName(), e.getMessage());
        } finally {
            if (ObjectUtil.isNotNull(bis)) {
                try {
                    bis.close();
                } catch (IOException e) {
                    log.error("用户 {} 下载归档文件失败： {}", loginUser.getRealName(), e.getMessage());
                }
            }
            if (ObjectUtil.isNotNull(fis)) {
                try {
                    fis.close();
                } catch (IOException e) {
                    log.error("用户 {} 下载归档文件失败： {}", loginUser.getRealName(), e.getMessage());
                }
            }
        }
    }

    @ApiOperation("专业归档 删除已经上传的文件")
    @PostMapping("/deleteUploadedFile")
    public ResponseMessage deleteUploadedFile(String path) {
        User loginUser = JSON.parseObject(JSONUtil.toJsonStr(SecurityContextHolder.getContext().getAuthentication().getPrincipal()), User.class);
        boolean result = false;
        //解密路径
        String filePath = archiveFilePath + File.separator + aes.decryptStr(path, CharsetUtil.CHARSET_UTF_8);
        log.info("解密后文件路径: {}", filePath);
        // 截取文件名
        String fileName = StrUtil.subAfter(filePath, File.separator, true);
        try {
            // 输入流
            File file = new File(filePath);
            result = file.delete();
            if (file.exists()) {
                result = false;
                log.error("用户  {}  删除归档文件失败: {}", loginUser.getRealName(), file.getName());
            } else {
                //os正常写入了
                log.info("用户 {} 删除归档文件成功: {}", loginUser.getRealName(), fileName);
            }
            if (majorArchiveService.deleteUploadedFileRecord(loginUser, path) <= 0) {
                return ResponseMessage.newErrorInstance("文件已经不存在");
            }
        } catch (Exception e) {
            log.error("用户：{}   删除归档文件失败: {}", loginUser.getRealName(), e.getMessage());
        }
        return ResponseMessage.newSuccessInstance(result);
    }


    @ApiOperation("专业归档 获取自己上传的所有文件（包括批次名，不同批次里的文件信息等）")
    @GetMapping("/getUploadedFilesInfo")
    public ResponseMessage<List<MajorArchiveGetUploadedFilesInfoResult>> getUploadedFilesInfo() {
        return ResponseMessage.newSuccessInstance(majorArchiveService.getUploadedFilesInfo());
    }


    @ApiOperation("专业归档 获取模板文件信息")
    @GetMapping("/getTemplateFilesInfo")
    public ResponseMessage<List<MajorArchiveGetTemplateFilesInfoResult>> getTemplateFilesInfo() {
        return ResponseMessage.newSuccessInstance(majorArchiveService.getTemplateFilesInfo());
    }


    @ApiOperation("专业归档负责人 上传模板文件")
    @PostMapping("/uploadTemplateFile")
    public ResponseMessage uploadTemplateFile(@RequestPart MultipartFile file) throws IOException {
        // 获取当前登陆用户(课程负责人)对象
        User loginUser = JSON.parseObject(JSONUtil.toJsonStr(SecurityContextHolder.getContext().getAuthentication().getPrincipal()), User.class);
        if (ObjectUtil.isNull(file)) {
            return ResponseMessage.newErrorInstance("请选择需要上传的文件");
        } else if (StrUtil.isBlank(file.getOriginalFilename())) {
            return ResponseMessage.newErrorInstance("请选择需要上传的文件");
        }
        // 目录添加uuid防重
        String uuid = IdUtil.simpleUUID();
        File path = new File(templatePath + File.separator + uuid);
        if (!path.isDirectory()) {
            path.mkdirs();
        }
        // 加密文件名
        String encryptFileName = aes.encryptHex(uuid + File.separator + file.getOriginalFilename());
        String filePath = templatePath + File.separator + uuid + File.separator + file.getOriginalFilename();
        log.info("用户 :{}   上传专业归档模板文件 : {}    加密路径 :  {}", loginUser.getRealName(), file.getOriginalFilename(), encryptFileName);
        //进行信息存储
        if (majorArchiveService.uploadTemplateFileRecord(loginUser, file.getOriginalFilename(), encryptFileName) >= 0) {
            FileUtil.writeBytes(file.getBytes(), filePath);
            log.info("用户 :{}   上传专业归档模板文件  : {}   上传成功", loginUser.getRealName(), file.getOriginalFilename());
            return ResponseMessage.newSuccessInstance("上传文件成功");
        } else {
            log.info("用户 :{}   上传专业归档模板文件  : {}   上传失败", loginUser.getRealName(), file.getOriginalFilename());
            return ResponseMessage.newErrorInstance("上传文件失败");
        }
    }


    @ApiOperation("专业归档负责人 删除模板文件")
    @PostMapping("/deleteTemplateFile")
    public ResponseMessage deleteTemplateFile(String path) {
        User loginUser = JSON.parseObject(JSONUtil.toJsonStr(SecurityContextHolder.getContext().getAuthentication().getPrincipal()), User.class);
        boolean result = false;
        String filePath = templatePath + File.separator + aes.decryptStr(path, CharsetUtil.CHARSET_UTF_8);
        log.info("解密后文件路径: {}", filePath);
        // 截取文件名
        String fileName = StrUtil.subAfter(filePath, File.separator, true);
        try {
            // 输入流
            File file = new File(filePath);
            result = file.delete();
            if (file.exists()) {
                result = false;
                log.error("用户{}删除课程自评报告失败: {}", loginUser.getRealName(), file.getName());
            } else {
                //os正常写入了
                log.info("用户 {} 删除课程自评报告成功: {}", loginUser.getRealName(), fileName);
            }
            if (majorArchiveService.deleteTemplateFileRecord(fileName, path) <= 0) {
                return ResponseMessage.newErrorInstance("文件已经不存在");
            }
        } catch (Exception e) {
            log.error("用户{}删除课程自评报告失败: {}", loginUser.getRealName(), e.getMessage());
        }
        return ResponseMessage.newSuccessInstance(result);
    }


    //根据模板path下载模板
    @ApiOperation("专业归档 根据模板文件path下载模板")
    @GetMapping("/downloadTemplateFile")
    public void downloadTemplateFile(String path, HttpServletRequest request, HttpServletResponse response) {
        User loginUser = JSON.parseObject(JSONUtil.toJsonStr(SecurityContextHolder.getContext().getAuthentication().getPrincipal()), User.class);
        //解密路径
        String filePath = templatePath + File.separator + aes.decryptStr(path, CharsetUtil.CHARSET_UTF_8);
        log.info("解密后文件路径: {}", filePath);
        // 截取文件名
        String fileName = StrUtil.subAfter(filePath, File.separator, true);
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            // 配置文件下载及避免呢中午呢乱码
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            // 输入流
            bis = new BufferedInputStream(new FileInputStream(filePath));
            // 输出流
            OutputStream os = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int temp = 0;
            // 每次读取的字符串长度
            while ((temp = bis.read(buffer)) != -1) {
                os.write(buffer, 0, temp);
            }
            //os正常写入了
            log.info("用户 {} 下载专业归档模板文件成功: {}", loginUser.getRealName(), fileName);
        } catch (Exception e) {
            log.error("用户 {} 下载专业归档模板文件失败: {}", loginUser.getRealName(), e.getMessage());
        } finally {
            if (ObjectUtil.isNotNull(bis)) {
                try {
                    bis.close();
                } catch (IOException e) {
                    log.error("{}", e.getMessage());
                }
            }
            if (ObjectUtil.isNotNull(fis)) {
                try {
                    fis.close();
                } catch (IOException e) {
                    log.error("{}", e.getMessage());
                }
            }
        }
    }


    @ApiOperation("专业归档负责人 根据专业批次提交评审")
    @PostMapping("/submitEvaluation")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "opinion", value = "负责人评审意见", required = true, paramType = "query"),
            @ApiImplicitParam(name = "batchName", value = "评审的批次名字", required = true, paramType = "query"),
            @ApiImplicitParam(name = "majorName", value = "评审的专业名字", required = true, paramType = "query")
    })
    public ResponseMessage submitEvaluation(String opinion, String batchName, String majorName) {
        return ResponseMessage.newSuccessInstance(majorArchiveService.submitEvaluation(opinion, batchName, majorName));
    }


    @ApiOperation("专业归档负责人  获取管理的专业与审批批次，评审状态")
    @GetMapping("/getManageMajorAndBatchInfo")
    public ResponseMessage<List<MajorArchiveGetManageMajorAndBatchInfoResult>> getManageMajorAndBatchInfo() {
        return ResponseMessage.newSuccessInstance(majorArchiveService.getManageMajorAndBatchInfo());
    }


    @ApiOperation("专业归档负责人 获取负责的专业批次的文件信息")
    @GetMapping("/getBatchFilesInfo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "batchName", value = "评审批次", required = true, paramType = "query"),
            @ApiImplicitParam(name = "majorId", value = "评审的专业id", required = true, paramType = "query")
    })
    public ResponseMessage<List<MajorArchiveGetBatchFilesInfoResult>> getBatchFilesInfo(String batchName, Integer majorId) {
        return ResponseMessage.newSuccessInstance(majorArchiveService.getBatchFilesInfo(batchName, majorId));
    }


    @ApiOperation("专业归档负责人 根据专业和批次获取已经评审的信息opinion")
    @GetMapping("/getEvaluatedBatchInfo")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "batchName", value = "评审批次", required = true, paramType = "query"),
            @ApiImplicitParam(name = "majorId", value = "评审的专业id", required = true, paramType = "query")
    })
    public ResponseMessage<MajorArchiveGetEvaluatedBatchInfoResult> getEvaluatedBatchInfo(String batchName, Integer majorId) {
        MajorArchiveGetEvaluatedBatchInfoResult result = majorArchiveService.getEvaluatedBatchInfo(batchName, majorId);
        if (result == null) {
            return ResponseMessage.newSuccessInstance("未评审或查询不到内容");
        }
        return ResponseMessage.newSuccessInstance(result);
    }


}
