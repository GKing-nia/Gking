package com.vtmer.microteachingquality.model.dto.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@ApiModel("文件列表")
@AllArgsConstructor
@NoArgsConstructor
public class FileInfoResult {

    @ApiModelProperty("文件id")
    private Integer fileId;

    @ApiModelProperty("文件名字")
    private String fileName;

    @ApiModelProperty("文件加密路径")
    private String filePath;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
}
