package com.vtmer.microteachingquality.model.dto.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("课程组长账号下属组员的评价指标以及对应的课程的信息")
public class ClazzLeaderGetMembersEvaluationInfoResult {

    @ApiModelProperty("下属id")
    private Integer memberId;

    @ApiModelProperty("下属名字")
    private String memberName;

    @ApiModelProperty("下属负责的课程信息List")
    private List<ClazzLeaderGetMembersEvaluationInfoMemberClazzResult> memberClazz;

}
