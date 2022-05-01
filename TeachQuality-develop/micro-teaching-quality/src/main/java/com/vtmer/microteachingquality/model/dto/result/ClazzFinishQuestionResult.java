package com.vtmer.microteachingquality.model.dto.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@ApiModel("查看评审结果数据集")
@AllArgsConstructor
public class ClazzFinishQuestionResult {

    @ApiModelProperty("问题id")
    private Integer optionId;

    @ApiModelProperty("一级指标")
    private String firstTarget;

    @ApiModelProperty("具体细节")
    private String detail;

    @ApiModelProperty("具体选项")
    private String mark;
}
