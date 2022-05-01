package com.vtmer.microteachingquality.service;

import com.vtmer.microteachingquality.model.dto.insert.LeaderEvaluateDTO;
import com.vtmer.microteachingquality.model.dto.insert.MasterEvaluateDTO;
import com.vtmer.microteachingquality.model.dto.result.*;
import com.vtmer.microteachingquality.model.dto.select.GetAllMajorsDTO;
import com.vtmer.microteachingquality.model.dto.select.MajorDTO;
import com.vtmer.microteachingquality.model.entity.Report;
import com.vtmer.microteachingquality.model.entity.ReportTemplate;
import com.vtmer.microteachingquality.model.entity.User;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;

public interface ReportService {

    /**
     * 新增自评报告模版
     *
     * @param reportTemplate
     * @return
     */
    int saveReportTemplate(ReportTemplate reportTemplate);

    /**
     * 新增自评报告记录
     *
     * @param report
     * @return
     */
    int saveReportRecord(Report report);

    /**
     * 根据专业获取自评报告模版
     *
     * @param major
     * @return
     */
    ReportTemplate getReportTemplateByMajor(String major);

    /**
     * 修改自评报告记录
     *
     * @param loginUser
     * @param report
     * @return
     */
    int updateReportRecord(User loginUser, Report report);

    /**
     * 获取所有专业自评报告
     *
     * @return
     */
    List<Report> listReport();

    /**
     * 根据专业获取自评报告
     *
     * @param major
     * @return
     */
    LeaderGetReportResult getReportByMajor(String major);

    /**
     * 根据报告id获取自评报告
     *
     * @param reportId
     * @return
     */
    Report getReportById(Integer reportId);

    /**
     * 根据路径获取自评报告
     *
     * @param path
     * @return
     */
    Report getReportByPath(String path);

    /**
     * 获取所有评估指标
     *
     * @return
     */
    List<OptionResult> listOption();

    /**
     * 根据课程类型获取所有指标
     *
     * @param majorName
     * @return
     */
    List<OptionResult> listOptionByType(String majorName);

    /**
     * 新增评审专家评审信息
     *
     * @param loginUser
     * @param masterEvaluateDTO
     * @return
     */
    int saveMasterEvaluation(User loginUser, String majorName, MasterEvaluateDTO masterEvaluateDTO);

    /**
     * 新增专家组长评审信息
     *
     * @param userId
     * @param leaderEvaluateDTO
     * @return
     */
    int saveLeaderEvaluation(Integer userId, LeaderEvaluateDTO leaderEvaluateDTO);

    /**
     * 评审专家获取所有组员
     *
     * @return
     */
    List<MasterInfoResult> listMaster(User loginLeader);

    /**
     * 根据组员用户id获取评审专家评估信息
     *
     * @param userId
     * @return
     */
    MasterEvaluationResult getMasterEvaluationByUserIdAndMajorName(Integer userId, String majorName, boolean isLeader);

    /**
     * 根据组长自身id获取组长评估信息
     *
     * @param userId
     * @return
     */
    LeaderEvaluationResult getMasterEvaluationByUserIdAndMajorName(Integer userId, String majorName);

    /**
     * 专家组长根据用户id和专业id退回评审专家评估信息
     *
     * @param userId
     * @return
     */
    int cancelEvaluation(Integer userId, String majorName);

    /**
     * 修改专业报告最终结果(合格/不合格)
     *
     * @param major
     * @param result
     * @return
     */
    int updateReportResult(String major, String result);

    /**
     * 获取所有专业和专业的类型
     *
     * @return
     */
    List<GetAllMajorsAndTypesResult> getAllMajorsAndTypes(MajorDTO majorDTO);

    /**
     * 获取所有的专业，不包含专业类型
     *
     * @return
     */
    GetAllMajorsDTO getAllMajors(MajorDTO majorDTO);

    /**
     * 专家获取需要评审的专业信息
     *
     * @return
     */
    List<ExpertGetMajorInfoResult> getMajorInfo();

    /**
     * 根据专业名称获取对应自评报告文件信息
     */
    GetFileInfoResult getFileInfo(String majorName);


    /**
     * 获取用户未评审的专业信息
     */
    ExpertGetNotEvaluatedInfoResult getNotEvaluatedInfo();

    /**
     * 获取用户已经评审的专业信息
     */
    ExpertGetEvaluatedInfoResult getEvaluatedInfo();

    XSSFWorkbook exportRecord(Integer userId);

    MasterEvaluationResult getMasterEvaluationByUserIdAndMajorNameWithCache(Integer userId, String majorName, boolean isLeader);
}
