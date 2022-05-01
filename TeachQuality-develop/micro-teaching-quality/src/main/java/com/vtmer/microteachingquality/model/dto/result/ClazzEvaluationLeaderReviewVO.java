package com.vtmer.microteachingquality.model.dto.result;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Hung
 * @date 2022/4/24 22:28
 */
@Data
@AllArgsConstructor
@ApiModel
public class ClazzEvaluationLeaderReviewVO {
    Integer userId;
    String realName;
    String opinion;
    LocalDateTime updateTime;
}
