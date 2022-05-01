package com.vtmer.microteachingquality.model.dto.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("获取用户未评审的课程信息")
public class ExpertGetNotEvaluatedInfoResult {

    @ApiModelProperty("待评审课程总数")
    private Integer totalCounts;

    @ApiModelProperty("具体专业信息List")
    private List<ExpertGetNotEvaluatedInfoMajorsInfoResult> majorsInfo;

}
