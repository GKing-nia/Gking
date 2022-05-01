package com.vtmer.microteachingquality.model.dto.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
@ApiModel("评审专家评审信息传输对象")
public class MasterEvaluationResult {

    @ApiModelProperty(value = "评估状态", notes = "未填写/待审阅/已审阅")
    private String status;

    @ApiModelProperty(value = "评审选项结果集", notes = "传输格式: '选项id: 达到程度(达到/基本达到/未达到)'")
    private Map<Integer, String> optionMap;

    @ApiModelProperty(value = "评审意见")
    private String opinion;

}
