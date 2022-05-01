package com.vtmer.microteachingquality.mapper;

import com.vtmer.microteachingquality.model.entity.Report;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReportMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Report record);

    Report selectByPrimaryKey(Integer id);

    List<Report> selectAll();

    int updateByPrimaryKey(Report record);

    /**
     * 插入自评报告创建信息
     *
     * @param report
     * @return
     */
    int insertReport(Report report);

    /**
     * 修改自评报告信息
     *
     * @param report
     * @return
     */
    int updateReportById(Report report);

    /**
     * 根据专业查询自评报告
     *
     * @param major
     * @return
     */
    Report selectByMajor(String major);

    /**
     * 根据路径查询自评报告
     *
     * @param path
     * @return
     */
    Report selectByPath(String path);

    /**
     * 根据专业修改自评报告最终结果
     *
     * @param major
     * @param result
     * @return
     */
    int updateReportByMajor(String major, String result);

    /**
     * 根据专业删除自评报告
     *
     * @param major
     * @return
     */
    int deleteByMajor(String major);

}