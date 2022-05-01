package com.vtmer.microteachingquality.common.constant.enums;

/**
 * @author 陈沛泓
 */
public interface EvaluationProcessStatus {

    /**
     * 1代表创建流程完成，2代表提交评审材料，3代表专家评审，4代表专家组长评审，5代表专家小组给出评审意见，6代表流程结束,7表示评审材料退回
     */
    String EVALUATION_PROCESS_ESTABLISHED = "1";
    String EVALUATION_PROCESS_SUBMIT_MATERIAL = "2";
    String EVALUATION_PROCESS_EXPERT_REVIEW = "3";
    String EVALUATION_PROCESS_EXPERT_LEADER_REVIEW = "4";
    String EVALUATION_PROCESS_EXPERT_LEADER_OPINION = "5";
    String EVALUATION_PROCESS_END = "6";
    String EVALUATION_PROCESS_MATERIAL_BACK = "7";

}
