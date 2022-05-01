package com.vtmer.microteachingquality.service;


import com.vtmer.microteachingquality.model.dto.select.ClazzTemplateDTO;
import com.vtmer.microteachingquality.model.dto.select.GetAllClazzTemplateDTO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface CourseReportService {

    /**
     * 新增课程自评报告模板
     *
     * @param file 模板文件
     * @return 返回模板文件加密后的路径
     */
    Integer saveCourseReportTemplate(MultipartFile file);


    /**
     * 返回课程自评报告模板
     */
    void getCourseReportTemplate(Integer id, HttpServletRequest request, HttpServletResponse response);


    /**
     * 修改课程自评报告模板
     *
     * @param id
     * @param file
     * @return
     */
    Integer updateCourseReportTemplate(Integer id, MultipartFile file);

    /**
     * 获取所有课程自评模板
     *
     * @param clazzTemplateDTO
     * @return
     */
    GetAllClazzTemplateDTO listCourseReportTemplate(ClazzTemplateDTO clazzTemplateDTO);

    /**
     * 根据id删除课程自评模板
     *
     * @param id
     * @return
     */
    int deleteCourseReportTemplate(Integer id);
}
