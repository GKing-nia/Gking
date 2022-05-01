package com.vtmer.microteachingquality.model.dto.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("专业与学院DTO")
@Data
public class GetAllMajorsResult {

    @ApiModelProperty("专业名字")
    private String name;

    @ApiModelProperty("学院")
    private String college;

    @ApiModelProperty("专业模板文件名")
    private String fileName;

    @ApiModelProperty("文件加密路径")
    private String filePath;
}
