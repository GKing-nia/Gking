package com.vtmer.microteachingquality.model.dto.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("通过课程评审专家id获取这个专家所有要评审的课程信息")
public class GetEvaluationClazzByUserIdResult {

    @ApiModelProperty("课程id")
    private Integer clazzId;

    @ApiModelProperty("课程所属学院")
    private String college;

    @ApiModelProperty("课程所属专业")
    private String clazzMajor;

    @ApiModelProperty("课程名字")
    private String clazzName;

    @ApiModelProperty("课程类型")
    private String clazzType;

    @ApiModelProperty("课程的评审状态：0未评审，1已经评审")
    private String clazzStatus;
}
