package com.vtmer.microteachingquality.model.dto.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author 墨小小
 * @date 21-09-24 21:13
 */
@Data
@ApiModel("专业归档 获取所有文件（包括批次名，不同批次里的文件信息等）  文件信息List")
public class MajorArchiveGetUploadedFilesInfoFileListResult {

    @ApiModelProperty("文件id")
    private Integer id;

    @ApiModelProperty("文件路径")
    private String path;

    @ApiModelProperty("文件名字")
    private String fileName;

    @ApiModelProperty("更新时间")
    private Date updateTime;

}
