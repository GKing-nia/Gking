package com.vtmer.microteachingquality.model.dto.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("评审专家信息传输对象")
public class MasterInfoResult {

    @ApiModelProperty("组长下属名字")
    private String memberName;

    @ApiModelProperty("组长下属的id")
    private Integer memberId;

    @ApiModelProperty("下属负责的专业信息list")
    private List<MasterInfoMajorInfoResult> majorInfo;

}
