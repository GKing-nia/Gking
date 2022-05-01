package com.vtmer.microteachingquality.model.dto.select;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("分页获取所有专业")
public class MajorDTO {

    @ApiModelProperty("页数，从1开始")
    private Integer pageIndex;

    @ApiModelProperty("页面长度")
    private Integer pageLength;
}
