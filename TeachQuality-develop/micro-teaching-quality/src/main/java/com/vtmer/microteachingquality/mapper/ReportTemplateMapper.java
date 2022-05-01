package com.vtmer.microteachingquality.mapper;

import com.vtmer.microteachingquality.model.entity.ReportTemplate;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReportTemplateMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(ReportTemplate record);

    ReportTemplate selectByPrimaryKey(Integer id);

    List<ReportTemplate> selectAll();

    int updateByPrimaryKey(ReportTemplate record);

    /**
     * 插入自评报告模版信息
     *
     * @param reportTemplate
     * @return
     */
    int insertReportTemplate(ReportTemplate reportTemplate);

    /**
     * 根据专业查询自评报告模版
     *
     * @param major
     * @return
     */
    ReportTemplate selectByMajor(String major);

    /**
     * 根据专业删除自评报告模版
     *
     * @param major
     * @return
     */
    int deleteByMajor(String major);

}