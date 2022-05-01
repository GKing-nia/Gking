package com.vtmer.microteachingquality.model.dto.insert;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Hung
 */
@Data
@ApiModel("课程评审专家上传答题记录")
@AllArgsConstructor
@NoArgsConstructor
public class SubmitEvaluationRecordBO {

    @ApiModelProperty("具体答题记录List")
    private List<InsertEvaluationRecordDTO> insertEvaluationRecordDTOList;

    @ApiModelProperty("评审流程id")
    private Long evaluationId;

    @ApiModelProperty("因为专家组长可以修改专家的评审，所以这里为 原来专家评审的用户id，如果为新增评审，就为专家的id")
    private Integer originReviewExpertId;

    @ApiModelProperty("专家意见")
    private String advantage;

    @ApiModelProperty("专家问题")
    private String problem;

    @ApiModelProperty("专家意见")
    private String advice;
}
