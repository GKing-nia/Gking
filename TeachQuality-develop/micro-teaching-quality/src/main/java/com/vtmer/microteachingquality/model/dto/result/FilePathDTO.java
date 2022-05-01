package com.vtmer.microteachingquality.model.dto.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("文件下载返回DTO")
public class FilePathDTO {

    @ApiModelProperty("文件路径")
    private String path;

    @ApiModelProperty("文件名")
    private String fileName;
}
