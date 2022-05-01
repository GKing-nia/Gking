package com.vtmer.microteachingquality.service;


import com.vtmer.microteachingquality.model.dto.insert.SubmitEvaluationRecordBO;
import com.vtmer.microteachingquality.model.dto.result.ClazzFinishResult;
import com.vtmer.microteachingquality.model.dto.result.ClazzInJudgeResultDTO;
import com.vtmer.microteachingquality.model.dto.result.FinishedClazzReviewVO;
import com.vtmer.microteachingquality.model.dto.result.GetClazzFilesResult;

import java.util.List;

public interface CourseEvaluationExpertService {
    /**
     * 获取评审课程报告信息
     *
     * @param userId
     * @return 返回课程报告信息
     */
    FinishedClazzReviewVO selectCourseMessageofEvaluationExpert(Integer userId, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 获取评审信息
     *
     * @param clazzId
     * @return 返回评审信息
     */

    List<ClazzInJudgeResultDTO> getClazzInJudgeByClazzType(Integer clazzId);

    /**
     * 专家评审获取自己的评审信息
     */
    ClazzFinishResult getSingleClazzFinishReview(Long evaluationId, Integer userId);

    /**
     * @author 达
     * 课程评审专家提交评审记录
     */
    Boolean insertEvaluationRecord(SubmitEvaluationRecordBO submitEvaluationRecordBO);

    /**
     * 根据课程名字返回文件列表
     *
     * @param evaluationId
     * @return
     */
    List<GetClazzFilesResult> getAllEvaluationFiles(Long evaluationId);

    /**
     * 插入空白数据用接口
     *
     * @return
     */
    Integer insertEmptyData(String tableName, Integer size);
}
