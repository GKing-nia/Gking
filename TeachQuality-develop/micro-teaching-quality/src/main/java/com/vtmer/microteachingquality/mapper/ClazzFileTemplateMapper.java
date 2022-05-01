package com.vtmer.microteachingquality.mapper;

import com.vtmer.microteachingquality.model.entity.ClazzFileTemplate;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ClazzFileTemplateMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ClazzFileTemplate record);

    int insertSelective(ClazzFileTemplate record);

    ClazzFileTemplate selectByPrimaryKey(Integer id);

    ClazzFileTemplate selectByMajor(String major);

    List<ClazzFileTemplate> selectAll();

    int updateByPrimaryKeySelective(ClazzFileTemplate record);

    int updateByPrimaryKey(ClazzFileTemplate record);

    int saveCourseReportTemplate(ClazzFileTemplate template);

    int updateCourseReportTemplate(ClazzFileTemplate template);

    int countCourseReportTemplate();

    List<ClazzFileTemplate> listCourseReportTemplate(Integer start, Integer size);
}