package com.vtmer.microteachingquality.model.dto.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("专家获取用户已经评审的专业信息")
public class ExpertGetEvaluatedInfoResult {

    @ApiModelProperty("总条目数")
    private Integer totalCounts;

    @ApiModelProperty("专业信息")
    private List majorsInfo;
}
