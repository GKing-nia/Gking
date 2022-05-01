package com.vtmer.microteachingquality.model.dto.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
@ApiModel("评审指标传输对象")
public class OptionResult {

    @ApiModelProperty(value = "一级指标")
    private String firstTarget;

    @ApiModelProperty(value = "具体内容及其id")
    private Map<Integer, String> details;

}
