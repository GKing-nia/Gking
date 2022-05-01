package com.vtmer.microteachingquality.model.dto.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("专家获取用户已经评审的专业信息 专业信息")
public class ExpertGetEvaluatedInfoMajorsInfoResult {

    @ApiModelProperty("专业id")
    private Integer majorId;

    @ApiModelProperty("专业名字")
    private String majorName;

    @ApiModelProperty("专业所属学院")
    private String majorCollege;

    @ApiModelProperty("专业类型")
    private String majorType;

    @ApiModelProperty("评审状态")
    private String stauts;


}
