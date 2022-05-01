package com.vtmer.microteachingquality.model.dto.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("课程组长获取待评价的课程信息的课程信息")
public class NotEvaluateClazzInfoResult {

    @ApiModelProperty("课程id")
    private Integer clazzId;

    @ApiModelProperty("课程名字")
    private String clazzName;

}
