package com.vtmer.microteachingquality.model.dto.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel("课程负责人获取上传的文件信息")
@AllArgsConstructor
public class GetUploadedFilesResult {

    @ApiModelProperty("文件id")
    private Integer fileId;

    @ApiModelProperty("文件名字")
    private String fileName;

    @ApiModelProperty("文件路径")
    private String filePath;

    @ApiModelProperty("上传时间")
    private LocalDateTime updateTime;

}
