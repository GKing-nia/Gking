package com.vtmer.microteachingquality.model.dto.insert;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("课程评审专家上传评审记录")
public class InsertEvaluationRecordDTO {

    @ApiModelProperty("评审问题的id")
    private Integer optionId;

    @ApiModelProperty("问题的选项")
    private String mark;

}
