package com.vtmer.microteachingquality.model.dto.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("评审专家信息传输对象 下属负责的专业信息list")
public class MasterInfoMajorInfoResult {

    @ApiModelProperty("专业id")
    private Integer majorId;

    @ApiModelProperty("专业名字")
    private String majorName;

    @ApiModelProperty("评审状态")
    private String status;
}
