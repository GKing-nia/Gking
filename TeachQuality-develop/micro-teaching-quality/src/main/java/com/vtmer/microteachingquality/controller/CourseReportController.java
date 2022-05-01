package com.vtmer.microteachingquality.controller;

import com.vtmer.microteachingquality.common.ResponseMessage;
import com.vtmer.microteachingquality.model.dto.select.ClazzTemplateDTO;
import com.vtmer.microteachingquality.service.CourseReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@Api(tags = "课程自评报告相关接口")
@RequestMapping("/coursereport")
public class CourseReportController {

    @Autowired
    private CourseReportService courseReportService;

    @PostMapping("/schoolAccount")
    @ApiOperation("(学校账户)导入课程评价模板")
    public ResponseMessage<Integer> uploadCourseReport(@RequestPart MultipartFile file) {
        return ResponseMessage.newSuccessInstance(courseReportService.saveCourseReportTemplate(file), "上传课程评价模板成功");
    }

    @GetMapping("/schoolAccount/template/{id}")
    @ApiOperation("(学校账户)根据记录id返回课程评价模板文件")
    public void getCourseReportTemplate(@ApiParam(value = "模板id") @PathVariable("id") Integer id,
                                        HttpServletRequest request, HttpServletResponse response) {
        courseReportService.getCourseReportTemplate(id, request, response);
    }

    @PutMapping("/schoolAccount/template/{id}")
    @ApiOperation("(学校账户)修改课程评价模板文件")
    public ResponseMessage updateCourseReportTemplate(@ApiParam(value = "模板id") @PathVariable("id") Integer id,
                                                      @RequestPart MultipartFile file) {
        if (courseReportService.updateCourseReportTemplate(id, file) > 0) {
            return ResponseMessage.newSuccessInstance("修改课程评价模板文件成功");
        } else {
            return ResponseMessage.newErrorInstance("修改课程评价模板文件失败");
        }
    }

    @GetMapping("/schoolAccount/template")
    @ApiOperation("(学校账户)获取所有课程评价模板")
    public ResponseMessage<?> listCourseReportTemplate(ClazzTemplateDTO clazzTemplateDTO) {
        return ResponseMessage.newSuccessInstance(courseReportService.listCourseReportTemplate(clazzTemplateDTO), "获取所有课程评价模板成功");
    }

    @DeleteMapping("/schoolAccount/template/{id}")
    @ApiOperation("(学校账户)删除课程评价模板文件")
    public ResponseMessage deleteCourseReportTemplate(@ApiParam("记录id") @PathVariable("id") Integer id) {

        if (courseReportService.deleteCourseReportTemplate(id) > 0) {
            return ResponseMessage.newSuccessInstance("删除课程评价模板文件成功");
        }
        return ResponseMessage.newErrorInstance("删除课程评价模板文件失败");
    }
}
