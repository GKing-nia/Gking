package com.vtmer.microteachingquality.model.dto.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("课程组长获取待评价的课程信息")
public class ClazzLeaderGetNotEvaluateClazzInfoResult {

    @ApiModelProperty("课程信息")
    private List<NotEvaluateClazzInfoResult> clazzInfo;

    @ApiModelProperty("信息总条目数")
    private Integer totalSize;

}
