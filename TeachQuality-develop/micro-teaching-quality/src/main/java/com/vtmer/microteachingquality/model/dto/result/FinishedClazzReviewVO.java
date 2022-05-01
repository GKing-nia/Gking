package com.vtmer.microteachingquality.model.dto.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("分页获取待评审报告信息")
public class FinishedClazzReviewVO {

    @ApiModelProperty("评审报告信息list")
    private List<ClazzToReviewResult> clazzToReviewResultList;

    @ApiModelProperty("总条目数")
    private Integer totalCounts;

}
