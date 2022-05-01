package com.vtmer.microteachingquality.controller;

import cn.hutool.core.collection.CollUtil;
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
import com.vtmer.microteachingquality.common.constant.enums.ReportStatus;
import com.vtmer.microteachingquality.common.constant.enums.UserType;
import com.vtmer.microteachingquality.model.dto.insert.LeaderEvaluateDTO;
import com.vtmer.microteachingquality.model.dto.insert.MasterEvaluateDTO;
import com.vtmer.microteachingquality.model.dto.result.*;
import com.vtmer.microteachingquality.model.dto.select.MajorDTO;
import com.vtmer.microteachingquality.model.entity.Report;
import com.vtmer.microteachingquality.model.entity.ReportTemplate;
import com.vtmer.microteachingquality.model.entity.User;
import com.vtmer.microteachingquality.service.NotifyService;
import com.vtmer.microteachingquality.service.ReportService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "自评报告相关接口")
@RestController
@RequestMapping("/report")
@Slf4j
public class ReportController {

    /**
     * 随机生成存储加密文件名(自评报告)的密钥
     */
    private final byte[] ENCRYPT_KEY = "9fcccb8912be487e91dc2743d94e566a".getBytes();
    @Value("${report.template.path}")
    private String reportTemplatePath;
    @Value("${report.path}")
    private String reportPath;
    @Resource
    private ReportService reportService;
    @Resource
    private NotifyService notifyService;
    /**
     * 构建加密
     */
    private AES aes = SecureUtil.aes(ENCRYPT_KEY);

    @ApiOperation("(学校账号)上传各专业自评报告模版")
    @PostMapping("/schoolAccount")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "选择需要上传的自评报告模版文件", required = true, paramType = "query"),
            @ApiImplicitParam(name = "college", value = "所属学院", required = true, paramType = "query"),
            @ApiImplicitParam(name = "major", value = "所属专业", required = true, paramType = "query")
    })
    public ResponseMessage uploadReport(@RequestPart("file") MultipartFile file,
                                        @RequestParam("college") String college,
                                        @RequestParam("major") String major) {
        File path = new File(reportTemplatePath);
        if (!path.isDirectory()) {
            path.mkdirs();
        }
        String filePath = reportTemplatePath + File.separator + file.getOriginalFilename();
        try {
            FileUtil.writeBytes(file.getBytes(), filePath);
            // 存储自评报告模版上传信息
            User loginUser = JSON.parseObject(JSONUtil.toJsonStr(SecurityContextHolder.getContext().getAuthentication().getPrincipal()), User.class);
            ReportTemplate reportTemplate = new ReportTemplate();
            reportTemplate.setUserId(loginUser.getId());
            reportTemplate.setCollege(college);
            reportTemplate.setMajor(major);
            reportTemplate.setPath(filePath);
            // 创建自评报告未完成填写记录
            Report report = new Report();
            report.setCollege(reportTemplate.getCollege());
            report.setMajor(reportTemplate.getMajor());
            report.setStatus(ReportStatus.NOT_FILL.getStatus());
            if (reportService.saveReportTemplate(reportTemplate) > 0 && reportService.saveReportRecord(report) > 0) {
                return ResponseMessage.newSuccessInstance("上传自评报告模版成功");
            } else {
                return ResponseMessage.newErrorInstance("上传自评报告模版失败");
            }
        } catch (Exception e) {
            log.error("上传自评报告模版失败: {}", e.getMessage());
            return ResponseMessage.newErrorInstance("上传自评报告模版失败");
        }

    }

    @ApiOperation("(专业负责人)查看自评报告模版信息")
    @GetMapping("/manager/template")
    public ResponseMessage<ReportTemplateResult> managerGetTemplate() {
        // 获取当前登陆用户(专业负责人)对象
        User loginUser = JSON.parseObject(JSONUtil.toJsonStr(SecurityContextHolder.getContext().getAuthentication().getPrincipal()), User.class);
        // 获取自评报告模版
        ReportTemplate reportTemplate = reportService.getReportTemplateByMajor(loginUser.getUserBelong());
        String fileName = null;
        if (ObjectUtil.isNotNull(reportTemplate)) {
            fileName = StrUtil.subAfter(reportTemplate.getPath(), File.separator, true);
        }
        ReportTemplateResult result = new ReportTemplateResult();
        BeanUtils.copyProperties(reportTemplate, result);
        result.setFileName(fileName);
        return ResponseMessage.newSuccessInstance(result);
    }

    @ApiOperation("(专业负责人)下载自评报告(模版)")
    @GetMapping("/manager/template/download")
    public ResponseMessage managerDownload(HttpServletResponse response
                                           //@ApiParam("选择硬盘下载目录") @RequestParam("downloadPath") String downloadPath
    ) {
        // 获取当前登陆用户(专业负责人)对象
        User loginUser = JSON.parseObject(JSONUtil.toJsonStr(SecurityContextHolder.getContext().getAuthentication().getPrincipal()), User.class);
        // 获取自评报告模版所属服务器路径
        ReportTemplate reportTemplate = reportService.getReportTemplateByMajor(loginUser.getUserBelong());
        String filePath;
        if (ObjectUtil.isNotNull(reportTemplate)) {
            filePath = reportTemplate.getPath();
        } else {
            return ResponseMessage.newErrorInstance("暂无本专业自评报告模版");
        }
        // 截取文件名
        String fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
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
            return ResponseMessage.newSuccessInstance("下载自评报告模版成功");
        } catch (Exception e) {
            log.error("用户{}下载自评报告模版失败: {}", loginUser.getRealName(), e.getMessage());
            return ResponseMessage.newErrorInstance("下载自评报告模版失败");
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


    @ApiOperation("(专业负责人)上传已填写的自评报告")
    @PostMapping("/manager")
    public ResponseMessage managerUpload(@ApiParam("选择需要上传的自评报告文件") @RequestPart MultipartFile file) {
        // 获取当前登陆用户(专业负责人)对象
        User loginUser = JSON.parseObject(JSONUtil.toJsonStr(SecurityContextHolder.getContext().getAuthentication().getPrincipal()), User.class);
        if (!loginUser.getUserType().equals(UserType.MANAGER.getType())) {
            return ResponseMessage.newErrorInstance("非专业负责人，无法上传对应专业自评报告");
        }
        if (ObjectUtil.isNull(file)) {
            return ResponseMessage.newErrorInstance("请选择需要上传的文件");
        } else if (StrUtil.isBlank(file.getOriginalFilename())) {
            return ResponseMessage.newErrorInstance("请选择需要上传的文件");
        }
        // 目录添加uuid防重
        String uuid = IdUtil.simpleUUID();
        File path = new File(reportPath + File.separator + uuid);
        if (!path.isDirectory()) {
            path.mkdirs();
        }
        // 加密文件名(
        String encryptFileName = aes.encryptHex(uuid + File.separator + file.getOriginalFilename());
        // String filePath = reportPath + File.separator + encryptFileName;
        String filePath = reportPath + File.separator + uuid + File.separator + file.getOriginalFilename();
        try {
            // 存储自评报告上传信息(修改自评报告上传者、状态、文件路径)
            Report report = new Report();
            report.setUserId(loginUser.getId());
            report.setStatus(ReportStatus.WAITING_EVALUATE.getStatus());
            report.setPath(encryptFileName);
            if (reportService.updateReportRecord(loginUser, report) > 0) {
                //通知专业评审专家进行评审
                notifyService.sendNotificationByMajorLeader(loginUser.getId());
                FileUtil.writeBytes(file.getBytes(), filePath);
                return ResponseMessage.newSuccessInstance("上传自评报告成功");
            } else {
                return ResponseMessage.newErrorInstance("上传自评报告失败");
            }
        } catch (IOException e) {
            log.error("上传自评报告失败: {}", e.getMessage());
            return ResponseMessage.newErrorInstance("上传自评报告失败");
        }
    }

    @ApiOperation("(学校账号)获取所有专业自评报告信息")
    @GetMapping("/schoolAccount")
    public ResponseMessage<List<Report>> listReport() {
        User loginUser = JSON.parseObject(JSONUtil.toJsonStr(SecurityContextHolder.getContext().getAuthentication().getPrincipal()), User.class);
        String userType = loginUser.getUserType();
        if (!userType.equals(UserType.SCHOOL.getType())) {
            return ResponseMessage.newErrorInstance("非学校账号，无法查看指定专业自评报告");
        }
        return ResponseMessage.newSuccessInstance(reportService.listReport());
    }


    //这个是返回文件的
    @ApiOperation("(学校账号/评审专家/专家组长)根据自评报告加密路径查看报告内容")
    @GetMapping("/preview/{path}")
    public void getReport(@ApiParam(value = "自评报告加密路径") @PathVariable("path") String path,
                          HttpServletResponse response) {
        Report report = reportService.getReportByPath(path);
        if (ObjectUtil.isNull(report) || report.getStatus().equals(ReportStatus.NOT_FILL)) {
            log.warn("学校未上传自评报告模版或专业负责人未完成填写自评报告，暂无法查看");
            return;
        }
        // 解密uuid+文件名，拼接路径
        String filePath = reportPath + File.separator + aes.decryptStr(path, CharsetUtil.CHARSET_UTF_8);
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
            // 这里有坑，直接filePath就行，不用new File(filePath)
            bis = new BufferedInputStream(new FileInputStream(new File(filePath)));
            // 输出流
            OutputStream os = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int temp = 0;
            // 每次读取的字符串长度
            while ((temp = bis.read(buffer)) != -1) {
                os.write(buffer, 0, temp);
            }
            log.info("下载自评报告成功");
        } catch (IOException e) {
            log.error("下载自评报告失败: {}", e.getMessage());
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

    //这个是返回路径和文件名的
/*    @ApiOperation("(学校账号/评审专家/专家组长)根据自评报告加密路径查看报告内容")
    @GetMapping("/preview/{path}")
    public ResponseMessage<FilePathDTO> getReport(@ApiParam(value = "自评报告加密路径") @PathVariable("path") String path,
                                                  HttpServletResponse response) {
        Report report = reportService.getReportByPath(path);
        if (ObjectUtil.isNull(report) || report.getStatus().equals(ReportStatus.NOT_FILL)) {
            return ResponseMessage.newErrorInstance("学校未上传自评报告模版或专业负责人未完成填写自评报告，暂无法查看");
        }
        // 解密uuid+文件名，拼接路径
        String filePath = reportPath + File.separator + aes.decryptStr(path, CharsetUtil.CHARSET_UTF_8);
        logger.info("解密后文件路径: {}", filePath);
        // 截取文件名
        String fileName = StrUtil.subAfter(filePath, File.separator, true);

        FilePathDTO filePathDTO = new FilePathDTO();
        filePath = reportPath + "/" + aes.decryptStr(path, CharsetUtil.CHARSET_UTF_8);
        fileName = StrUtil.subAfter(fileName, "/", true);
        filePathDTO.setPath(filePath);
        filePathDTO.setFileName(fileName);
        return ResponseMessage.newSuccessInstance(filePathDTO);
    }*/

    @ApiOperation("(评审专家/组长)获取专业自评报信息")
    @GetMapping("/evaluation")
    public ResponseMessage<LeaderGetReportResult> getReport(@ApiParam("要评审的专业名字") String majorName) {
        User loginUser = JSON.parseObject(JSONUtil.toJsonStr(SecurityContextHolder.getContext().getAuthentication().getPrincipal()), User.class);
        String userType = loginUser.getUserType();
        if (!userType.equals(UserType.MASTER.getType()) && !userType.equals(UserType.LEADER.getType())) {
            return ResponseMessage.newErrorInstance("非评审专家/组长，无法查看该专业自评报告");
        }
        return ResponseMessage.newSuccessInstance(reportService.getReportByMajor(majorName));
    }

//    @ApiOperation("(评审专家/组长)获取专业评估一级指标及其具体内容")
//    @GetMapping("/evaluation/option")
//    public ResponseMessage<List<OptionResult>> listOption() {
//        return ResponseMessage.newSuccessInstance(reportService.listOption());
//    }


    @ApiOperation("(评审专家/组长)根据用户的专业类型获取专业评估一级指标及其具体内容")
    @GetMapping("/evaluation/option")
    public ResponseMessage<List<OptionResult>> listOptionByType(@ApiParam("要评审的专业名字") String majorName) {
        User loginUser = JSON.parseObject(JSONUtil.toJsonStr(SecurityContextHolder.getContext().getAuthentication().getPrincipal()), User.class);
        if (!loginUser.getUserType().equals(UserType.MASTER.getType()) && !loginUser.getUserType().equals(UserType.LEADER.getType())) {
            return ResponseMessage.newErrorInstance("用户类型不为评审专家/专家组长，无法获取");
        }
        return ResponseMessage.newSuccessInstance(reportService.listOptionByType(majorName));
    }


    @ApiOperation("获取所有专业和专业类型")
    @GetMapping("/evaluation/majorsAndType")
    public ResponseMessage<?> getAllMajorsAndTypes(MajorDTO majorDTO) {
        Object o = reportService.getAllMajorsAndTypes(majorDTO);
        if (ObjectUtil.isNotNull(o)) {
            return ResponseMessage.newSuccessInstance(o);
        } else {
            return ResponseMessage.newErrorInstance("查询不到");
        }
    }

//    @ApiOperation("获取所有专业与学院（不包含专业类型）")
//    @GetMapping("/evaluation/majors")
//    public ResponseMessage<List<GetAllMajorsResult>> getAllMajors() {
//        return ResponseMessage.newSuccessInstance(reportService.getAllMajors());
//    }

    @ApiOperation("获取所有专业与学院（不包含专业类型）和总条目数")
    @GetMapping("/evaluation/majors")
    public ResponseMessage<?> getAllMajors(MajorDTO majorDTO) {
        Object o = reportService.getAllMajors(majorDTO);
        if (ObjectUtil.isNotNull(o)) {
            return ResponseMessage.newSuccessInstance(o);
        } else {
            return ResponseMessage.newErrorInstance("查询不到");
        }
    }

    @ApiOperation("评审专家 专家组长 获取自己需要评审的专业信息")
    @GetMapping("/majorInfo")
    public ResponseMessage<List<ExpertGetMajorInfoResult>> getMajorInfo() {
        return ResponseMessage.newSuccessInstance(reportService.getMajorInfo());
    }

    @ApiOperation("(评审专家)评审专业报告")
    @PostMapping("/evaluation/master")
    public ResponseMessage masterEvaluate(@Validated @RequestBody MasterEvaluateDTO masterEvaluateDTO) {
        User loginUser = JSON.parseObject(JSONUtil.toJsonStr(SecurityContextHolder.getContext().getAuthentication().getPrincipal()), User.class);
        if (reportService.saveMasterEvaluation(loginUser, masterEvaluateDTO.getMajorName(), masterEvaluateDTO) > 0) {
            return ResponseMessage.newSuccessInstance("提交评估结果成功");
        } else {
            return ResponseMessage.newErrorInstance("提交评估结果失败");
        }
    }

    @ApiOperation("(专家组长)评审专业报告并确定是否及格")
    @PostMapping("/evaluation/leader")
    public ResponseMessage leaderEvaluate(@RequestBody @Validated LeaderEvaluateDTO leaderEvaluateDTO) {
        User loginUser = JSON.parseObject(JSONUtil.toJsonStr(SecurityContextHolder.getContext().getAuthentication().getPrincipal()), User.class);
        if (reportService.saveLeaderEvaluation(loginUser.getId(), leaderEvaluateDTO) > 0) {
            // 改变专业报告最终状态(合格/不合格)
            if (reportService.updateReportResult(leaderEvaluateDTO.getMajorName(), leaderEvaluateDTO.getResult()) > 0) {
                return ResponseMessage.newSuccessInstance("提交评估结果成功");
            }
        }
        return ResponseMessage.newErrorInstance("提交评估结果失败");
    }

    @ApiOperation("(专家组长)查看所有评审组员信息")
    @GetMapping("/evaluation/leader")
    public ResponseMessage<List<MasterInfoResult>> listMaster() {
        User loginUser = JSON.parseObject(JSONUtil.toJsonStr(SecurityContextHolder.getContext().getAuthentication().getPrincipal()), User.class);
        if (!loginUser.getUserType().equals(UserType.LEADER.getType())) {
            return ResponseMessage.newErrorInstance("非评审专家组长，无法查看组员评估情况");
        }
        List<MasterInfoResult> masterList = reportService.listMaster(loginUser);
        if (CollUtil.isEmpty(masterList)) {
            return ResponseMessage.newErrorInstance("暂无组员");
        }
        return ResponseMessage.newSuccessInstance(masterList);
    }

    @ApiOperation("(专家组长)根据组员用户id查看指定专家具体评估内容")
    @GetMapping("/evaluation/master/{userId}/{majorName}")
    public ResponseMessage listMasterEvaluation(@PathVariable("userId") Integer userId, @PathVariable("majorName") String majorName) {
        User loginUser = JSON.parseObject(JSONUtil.toJsonStr(SecurityContextHolder.getContext().getAuthentication().getPrincipal()), User.class);
        if (!loginUser.getUserType().equals(UserType.LEADER.getType())) {
            return ResponseMessage.newErrorInstance("非评审专家组长，无法查看组员评估情况");
        }
        // 判断是否为组长查看自身
        if (userId.equals(loginUser.getId())) {
            LeaderEvaluationResult result = reportService.getMasterEvaluationByUserIdAndMajorName(userId, majorName);
            return ResponseMessage.newSuccessInstance(result);
        } else {
            MasterEvaluationResult result = reportService.getMasterEvaluationByUserIdAndMajorName(userId, majorName, true);
            return ResponseMessage.newSuccessInstance(result);
        }

    }

    @ApiOperation("(评审专家)查看自身具体评估内容")
    @GetMapping("/evaluation/master")
    public ResponseMessage<MasterEvaluationResult> getMasterEvaluation(@ApiParam("要查询的专业名字") String majorName) {
        User loginUser = JSON.parseObject(JSONUtil.toJsonStr(SecurityContextHolder.getContext().getAuthentication().getPrincipal()), User.class);
        if (!loginUser.getUserType().equals(UserType.MASTER.getType())) {
            return ResponseMessage.newErrorInstance("非评审专家，无法查看评估情况");
        }
        MasterEvaluationResult masterEvaluationResult = reportService.getMasterEvaluationByUserIdAndMajorNameWithCache(loginUser.getId(), majorName, false);
        //MasterEvaluationResult masterEvaluationResult = reportService.getMasterEvaluationByUserIdAndMajorName(loginUser.getId(), majorName, false);
        return ResponseMessage.newSuccessInstance(masterEvaluationResult);
    }

    @ApiOperation("(专家组长)根据用户id退回评审专家评估结果")
    @PutMapping("/evaluation/master/{userId}/{majorName}")
    public ResponseMessage cancelEvaluation(@PathVariable("userId") Integer userId, @PathVariable("majorName") String majorName) {
        User loginUser = JSON.parseObject(JSONUtil.toJsonStr(SecurityContextHolder.getContext().getAuthentication().getPrincipal()), User.class);
        if (!loginUser.getUserType().equals(UserType.LEADER.getType())) {
            return ResponseMessage.newErrorInstance("非评审专家组长，无法退回评估结果");
        }
        if (reportService.cancelEvaluation(userId, majorName) > 0) {
            notifyService.sendNotificationByMajorExpert(loginUser.getId());
            return ResponseMessage.newSuccessInstance("退回评估结果成功");
        } else {
            return ResponseMessage.newErrorInstance("退回评估结果失败");
        }
    }


    @ApiOperation("根据专业名称获取对应自评报告文件信息")
    @GetMapping("/getEvaluationFile")
    public ResponseMessage<GetFileInfoResult> getFileInfoByMajorName(String majorName) {
        return ResponseMessage.newSuccessInstance(reportService.getFileInfo(majorName));
    }


    @ApiOperation("获取用户未评审的专业信息")
    @GetMapping("/getNotEvaluatedInfo")
    public ResponseMessage<ExpertGetNotEvaluatedInfoResult> getNotEvaluatedInfo() {
        return ResponseMessage.newSuccessInstance(reportService.getNotEvaluatedInfo());
    }

    @ApiOperation("获取用户已评审的专业信息")
    @GetMapping("/getEvaluatedInfo")
    public ResponseMessage<ExpertGetEvaluatedInfoResult> getEvaluatedInfo() {
        return ResponseMessage.newSuccessInstance(reportService.getEvaluatedInfo());
    }


    /**
     * 通用下载请求
     */
    private Map<String, InputStream> download(HttpServletResponse response,
                                              String fileName, String filePath,
                                              FileInputStream fis, BufferedInputStream bis) throws Exception {
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
        Map<String, InputStream> inputStreamMap = new HashMap<>();
        inputStreamMap.put("fis", fis);
        inputStreamMap.put("bis", bis);
        return inputStreamMap;
    }


}
