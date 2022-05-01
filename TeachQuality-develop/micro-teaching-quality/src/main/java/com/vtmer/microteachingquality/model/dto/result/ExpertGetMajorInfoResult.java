package com.vtmer.microteachingquality.model.dto.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("专业评审专家 获取自己评审的专业信息")
public class ExpertGetMajorInfoResult {

    @ApiModelProperty("专业id")
    private Integer majorId;

    @ApiModelProperty("专业名字")
    private String majorName;

    @ApiModelProperty("专业所属学院")
    private String majorCollege;

    @ApiModelProperty("自己的评审状态")
    private String status;

    @ApiModelProperty("专业负责人提交的文件路径")
    private String reportPath;
}
