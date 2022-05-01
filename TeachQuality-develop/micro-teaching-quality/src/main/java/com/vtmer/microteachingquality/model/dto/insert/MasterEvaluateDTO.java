package com.vtmer.microteachingquality.model.dto.insert;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
@ApiModel("评审专家提交专业报告评估传输对象")
public class MasterEvaluateDTO {

    @ApiModelProperty(value = "评审选项结果集   , 传输格式: '选项id: 达到程度(达到/基本达到/未达到)'", required = true)
    private Map<Integer, String> optionMap;

    @ApiModelProperty(value = "评审意见", required = true)
    private String opinion;

    @ApiModelProperty("要评审的专业名字")
    private String majorName;

}
