package com.vtmer.microteachingquality.model.dto.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("返回课程评价模板")
public class GetAllClazzTemplateResult {

    @ApiModelProperty("id")
    private Integer id;

    @ApiModelProperty("文件名")
    private String name;

    @ApiModelProperty("文件path")
    private String filePath;
}
