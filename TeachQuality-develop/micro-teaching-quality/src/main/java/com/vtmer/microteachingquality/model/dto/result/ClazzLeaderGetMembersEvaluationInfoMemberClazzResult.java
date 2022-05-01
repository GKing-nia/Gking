package com.vtmer.microteachingquality.model.dto.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("课程组长账号下属组员的评价指标以及对应的课程的信息 下属课程信息")
public class ClazzLeaderGetMembersEvaluationInfoMemberClazzResult {

    @ApiModelProperty("课程id")
    private Integer clazzId;

    @ApiModelProperty("课程名字")
    private String clazzName;

    @ApiModelProperty("课程所属学院")
    private String clazzCollege;

    @ApiModelProperty("下属对该课程评审状态：0未评审，1已经评审")
    private String status;

}
