package com.vtmer.microteachingquality.model.dto.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
public class LeaderEvaluationResult {

    @ApiModelProperty(value = "评审选项结果集", notes = "传输格式: '选项id: 达到程度(达到/基本达到/未达到)'")
    private Map<Integer, String> optionMap;

    @ApiModelProperty(value = "评审意见")
    private String opinion;

    @ApiModelProperty(value = "评审结果(合格/不合格)")
    private String result;

}
