package com.vtmer.microteachingquality.controller;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import com.vtmer.microteachingquality.common.ResponseMessage;
import com.vtmer.microteachingquality.common.constant.enums.UserType;
import com.vtmer.microteachingquality.model.dto.insert.ClazzBo;
import com.vtmer.microteachingquality.model.dto.result.ClazzTemplateResult;
import com.vtmer.microteachingquality.model.entity.Clazz;
import com.vtmer.microteachingquality.model.entity.ClazzAnnotation;
import com.vtmer.microteachingquality.model.entity.ClazzFileTemplate;
import com.vtmer.microteachingquality.model.entity.User;
import com.vtmer.microteachingquality.service.ClazzService;
import com.vtmer.microteachingquality.util.UserUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Hung
 */
@Api(tags = "课程自评报告相关接口")
@RestController
@RequestMapping("/clazz")
@Slf4j
public class ClazzController {

    /**
     * 随机生成存储加密文件名(课程自评报告)的密钥
     * 固定密钥，不然会出bug
     */
    private final byte[] ENCRYPT_KEY = "9fcccb8912be487e91dc2743d94e566a".getBytes();
    /**
     * 构建加密
     */
    private final AES aes = SecureUtil.aes(ENCRYPT_KEY);
    @Value("${clazz.path}")
    private String clazzPath;
    @Value("${coursereport.template.path}")
    private String reportTemplatePath;
    @Autowired
    private ClazzService clazzService;

    @ApiOperation("获取课程负责人需要提交的材料的提示")
    @GetMapping("/clazz/annotation")
    public ResponseMessage<ClazzAnnotation> getClazzAnnotation() {
        ClazzAnnotation annotation = clazzService.getClazzAnnotationById(1);
        return annotation == null ? ResponseMessage.newErrorInstance("无材料提示") : ResponseMessage.newSuccessInstance(annotation.getInfo());
    }


    @ApiOperation("获取材料模版的信息")
    @GetMapping("/template")
    public ResponseMessage<List<ClazzFileTemplate>> getClazzTemplate() {
        List<ClazzFileTemplate> clazzTemplate = clazzService.getClazzTemplate();
        return clazzTemplate == null ? ResponseMessage.newErrorInstance("无材料模版信息") : ResponseMessage.newSuccessInstance(clazzTemplate);
    }

    @ApiOperation("(课程负责人)查看课程自评报告模版信息")
    @GetMapping("/template/view")
    public ResponseMessage<ClazzTemplateResult> clazzTemplateView() {
        // 获取当前登陆用户(课程负责人)对象
        User loginUser = UserUtil.getCurrentUser();
        if (!loginUser.getUserType().equals(UserType.CLAZZ_PRINCIPAL.getType())) {
            return ResponseMessage.newErrorInstance("非课程负责人，无法获取对应专业自评报告模版");
        }
        // 获取课程自评报告模版
        ClazzFileTemplate clazzFileTemplate = clazzService.getClazzFileTemplateByMajor(loginUser.getUserBelong());
        String fileName = Optional.ofNullable(clazzFileTemplate)
                .map(clazzFileTemplate1 -> StrUtil.subAfter(clazzFileTemplate.getPath(), File.separator, true)).get();
        ClazzTemplateResult result = new ClazzTemplateResult();
        BeanUtils.copyProperties(clazzFileTemplate, result);
        result.setName(fileName);
        return ResponseMessage.newSuccessInstance(result);
    }

    @ApiOperation("下载课程自评报告(模版)")
    @GetMapping("/template/download")
    public void clazzTemplateDownload(@Validated @ApiParam("文件加密路径") String path, HttpServletResponse response) {
        User loginUser = UserUtil.getCurrentUser();
        //解密路径
        String filePath = reportTemplatePath + File.separator + aes.decryptStr(path, CharsetUtil.CHARSET_UTF_8);
        log.info("解密后文件路径: {}", filePath);
        // 截取文件名
        String fileName = StrUtil.subAfter(filePath, File.separator, true);
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            // 配置文件下载及避免乱码
            response.setHeader("content-type", "application/octet-stream");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));

            // 输入流
            bis = new BufferedInputStream(new FileInputStream(filePath));
            // 输出流
            OutputStream os = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int temp;
            // 每次读取的字符串长度
            while ((temp = bis.read(buffer)) != -1) {
                os.write(buffer, 0, temp);
            }
            //os正常写入了
            log.info("用户 {} id为{} 下载课程自评报告模版成功: {}", loginUser.getRealName(), loginUser.getId(), fileName);
        } catch (Exception e) {
            log.error("用户{}下载课程自评报告模版失败: {}", loginUser.getRealName(), e.getMessage());
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                log.error("ClazzController clazzTemplateDownload方法 数据传输连接关闭失败");
            }
        }
    }

    @ApiOperation("用户创建课程")
    @PostMapping("/createNewClazz")
    public ResponseMessage<String> createNewClazz(ClazzBo clazzBo) {
        Clazz clazz = new Clazz();
        BeanUtils.copyProperties(clazzBo, clazz);
        clazz.setUserId(UserUtil.getCurrentUser().getId());
        return clazz.insert() ? ResponseMessage.newSuccessInstance("用户创建课程成功") : ResponseMessage.newErrorInstance("用户创建课程失败");
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
