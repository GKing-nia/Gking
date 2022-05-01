package com.vtmer.microteachingquality.model.dto.insert;

import lombok.Data;

/**
 * @author Hung
 * @date 2022/4/24 22:18
 */
@Data
public class ClazzEvaluationLeaderBO {
    Integer clazzId;
    Long evaluationId;
    String evaluationOpinion;
}
