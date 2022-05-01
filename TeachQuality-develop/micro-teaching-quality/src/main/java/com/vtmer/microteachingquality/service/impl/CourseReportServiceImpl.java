package com.vtmer.microteachingquality.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.vtmer.microteachingquality.common.exception.coursereport.CourseReportTemplateFailUploadException;
import com.vtmer.microteachingquality.common.exception.coursereport.CourseReportTemplateNotExistException;
import com.vtmer.microteachingquality.common.util.DownloadUtil;
import com.vtmer.microteachingquality.mapper.ClazzExpertManageInfoMapper;
import com.vtmer.microteachingquality.mapper.ClazzFileTemplateMapper;
import com.vtmer.microteachingquality.mapper.ClazzMapper;
import com.vtmer.microteachingquality.model.dto.result.GetAllClazzTemplateResult;
import com.vtmer.microteachingquality.model.dto.select.ClazzTemplateDTO;
import com.vtmer.microteachingquality.model.dto.select.GetAllClazzTemplateDTO;
import com.vtmer.microteachingquality.model.entity.ClazzFileTemplate;
import com.vtmer.microteachingquality.model.entity.User;
import com.vtmer.microteachingquality.service.CourseReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class CourseReportServiceImpl implements CourseReportService {

    /**
     * 随机生成存储加密文件名(自评报告)的密钥
     */
    private final byte[] ENCRYPT_KEY = "9fcccb8912be487e91dc2743d94e566a".getBytes();
    @Autowired
    private ClazzFileTemplateMapper clazzFileTemplateMapper;
    @Autowired
    private ClazzExpertManageInfoMapper clazzExpertManageInfoMapper;
    @Autowired
    private ClazzMapper clazzMapper;
    /**
     * 构建加密
     */
    private AES aes = SecureUtil.aes(ENCRYPT_KEY);

    @Value("${coursereport.template.path}")
    private String reportTemplatePath;

    /**
     * 学校账户导入课程自评报告模板
     *
     * @param file 模板文件
     * @return
     */
    @Override
    public Integer saveCourseReportTemplate(MultipartFile file) {
        if (ObjectUtil.isNull(file)) {
            throw new CourseReportTemplateFailUploadException("模板文件未上传");
        }

        //添加uuid防重
        String uuid = IdUtil.simpleUUID();
        File path = new File(reportTemplatePath + File.separator + uuid);

        if (!path.isDirectory()) {
            path.mkdirs();
        }
        //加密文件名
        String encryptFileName = aes.encryptHex(uuid + File.separator + file.getOriginalFilename());
        String filePath = reportTemplatePath + File.separator + uuid + File.separator + file.getOriginalFilename();
        ClazzFileTemplate template = new ClazzFileTemplate();

        try {
            FileUtil.writeBytes(file.getBytes(), filePath);
            User user = JSON.parseObject(JSONUtil.toJsonStr(SecurityContextHolder.getContext().getAuthentication().getPrincipal()), User.class);

            template.setId(user.getId());
            template.setName(file.getOriginalFilename());
            template.setPath(encryptFileName);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(sdf.format(new Date()));
            template.setCreateTime(date);
            template.setUpdateTime(date);

            if (clazzFileTemplateMapper.saveCourseReportTemplate(template) <= 0) {
                throw new CourseReportTemplateFailUploadException("课程自评报告模板上传失败");
            }

        } catch (ParseException | IOException e) {
            throw new CourseReportTemplateFailUploadException("课程自评报告模板上传失败");
        }

        return template.getId();
    }

    /**
     * 学校账户通过模板id获取课程自评报告模板
     *
     * @param request
     * @param response
     */
    @Override
    public void getCourseReportTemplate(Integer id, HttpServletRequest request, HttpServletResponse response) {
        ClazzFileTemplate template = clazzFileTemplateMapper.selectByPrimaryKey(id);
        String filePath = reportTemplatePath + File.separator + aes.decryptStr(template.getPath(), CharsetUtil.CHARSET_UTF_8);
        log.info("解密之后的路径:{}", filePath);
        log.info("课程自评报告模板的文件名:{}", template.getName());
        DownloadUtil.downloadFile(template.getName(), filePath, response);
    }

    @Override
    public Integer updateCourseReportTemplate(Integer id, MultipartFile file) {
        if (ObjectUtil.isNull(file)) {
            throw new CourseReportTemplateFailUploadException("上传模板文件未上传");
        }
        //添加uuid防重
        String uuid = IdUtil.simpleUUID();
        File filePath = new File(reportTemplatePath + File.separator + uuid);

        if (!filePath.isDirectory()) {
            filePath.mkdirs();
        }
        //加密文件名
        String encryptFileName = aes.encryptHex(uuid + File.separator + file.getOriginalFilename());
        String reportPath = reportTemplatePath + File.separator + uuid + File.separator + file.getOriginalFilename();
        ClazzFileTemplate template = new ClazzFileTemplate();
        User user = JSON.parseObject(JSONUtil.toJsonStr(SecurityContextHolder.getContext().getAuthentication().getPrincipal()), User.class);

        try {
            FileUtil.writeBytes(file.getBytes(), reportPath);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(sdf.format(new Date()));
            template.setName(file.getOriginalFilename());
            template.setUpdateTime(date);
            template.setPath(encryptFileName);
            template.setUserId(user.getId());
            template.setId(id);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return clazzFileTemplateMapper.updateCourseReportTemplate(template);
    }

    @Override
    public GetAllClazzTemplateDTO listCourseReportTemplate(ClazzTemplateDTO clazzTemplateDTO) {
        GetAllClazzTemplateDTO dtoResult = new GetAllClazzTemplateDTO();
        List<GetAllClazzTemplateResult> resultList = new ArrayList<>();
        //获取总条数
        int size = clazzFileTemplateMapper.countCourseReportTemplate();
        //如果总条数为0，则直接返回
        if (size <= 0) {
            return dtoResult;
        }
        List<ClazzFileTemplate> templateList = clazzFileTemplateMapper.listCourseReportTemplate(clazzTemplateDTO.getStart(), clazzTemplateDTO.getSize());

        for (ClazzFileTemplate template : templateList) {
            GetAllClazzTemplateResult result = new GetAllClazzTemplateResult();
            result.setName(template.getName());
            result.setFilePath(template.getPath());
            result.setId(template.getId());
            resultList.add(result);
        }

        dtoResult.setList(resultList);
        dtoResult.setSize(size);

        return dtoResult;
    }

    @Override
    public int deleteCourseReportTemplate(Integer id) {
        ClazzFileTemplate template = clazzFileTemplateMapper.selectByPrimaryKey(id);
        if (ObjectUtil.isNull(template)) {
            throw new CourseReportTemplateNotExistException("课程自评模板文件不存在!");
        }
        //文件路径
        String filePath = reportTemplatePath + File.separator + aes.decryptStr(template.getPath(), CharsetUtil.CHARSET_UTF_8);
        File file = new File(filePath);

        //先删除文件
        if (file.delete()) {
            log.info("文件已被删除:{}", filePath);
            //文件夹路径
            String directoryPath = StrUtil.subBefore(filePath, File.separator, true);
            File directory = new File(directoryPath);
            //删除文件夹
            directory.delete();
        }

        return clazzFileTemplateMapper.deleteByPrimaryKey(id);
    }


    /**
     * 通过课程评审专家id获取所有要评审的课程信息
     *
     * @param userId
     * @return
     */
    public List getClazzFileByUserId(Integer userId) {
        //todo 自定义一下要返回的信息和内容，然后根据专家id获取要管理的id后再根据id获取clazz信息

        return null;
    }

}
