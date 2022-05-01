package com.vtmer.microteachingquality.model.dto.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("评审报告信息传输对象")
public class ClazzToReviewResult {

    @ApiModelProperty("课程id")
    private Integer clazzId;

    @ApiModelProperty(value = "学院")
    private String clazzCollege;

    @ApiModelProperty(value = "课程名字")
    private String clazzName;

    @ApiModelProperty(value = "负责人名字")
    private String principalName;


}
