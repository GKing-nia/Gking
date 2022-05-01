package com.vtmer.microteachingquality.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vtmer.microteachingquality.model.dto.insert.ClazzEvaluationLeaderBO;
import com.vtmer.microteachingquality.model.dto.result.ClazzEvaluationLeaderReviewVO;
import com.vtmer.microteachingquality.model.dto.result.FinishedReviewVO;
import com.vtmer.microteachingquality.model.dto.result.GetUploadedFilesResult;
import com.vtmer.microteachingquality.model.entity.EvaluationProcess;

import java.util.List;

/**
 * @author Hung
 * @date 2022/4/20 20:02
 */
public interface EvaluationProcessService extends IService<EvaluationProcess> {

    List<EvaluationProcess> getEvaluationProcesses(Integer userId, Integer clazzId);


    /**
     * 课程负责人获取自己上传的文件list
     *
     * @param userId
     * @param evaluationId
     * @return
     */
    List<GetUploadedFilesResult> getUploadedFiles(Integer userId, Long evaluationId);

    /**
     * 课程评审专家退回评审记录
     *
     * @param evaluationId 被退回的评审id
     * @return 退回结果
     */
    Boolean sendBackEvaluation(Long evaluationId);


    /**
     * 获取该评审所有已经评审的专家信息
     *
     * @param evaluation
     * @return
     */
    List<FinishedReviewVO> getAllFinishedReviews(Long evaluation);

    /**
     * 保存专家组长的评审内容
     */
    Boolean postClazzEvaluationLeaderReview(ClazzEvaluationLeaderBO evaluationLeaderBO);

    /**
     * 获取此评审流程的专家组长评审列表
     *
     * @param evaluationId
     * @return
     */
    List<ClazzEvaluationLeaderReviewVO> getClazzEvaluationLeaderReviews(Long evaluationId);


}
