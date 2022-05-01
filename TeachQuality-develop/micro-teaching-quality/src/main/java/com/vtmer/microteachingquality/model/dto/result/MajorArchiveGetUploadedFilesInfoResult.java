package com.vtmer.microteachingquality.model.dto.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 墨小小
 * @date 21-09-24 21:05
 */
@Data
@ApiModel("专业归档 获取所有文件（包括批次名，不同批次里的文件信息等）")
public class MajorArchiveGetUploadedFilesInfoResult {

    @ApiModelProperty("批次名字")
    private String batchName;

    @ApiModelProperty("该批次的评审状态：1已经评审，0未评审")
    private String status;

    @ApiModelProperty("文件信息list")
    private List<MajorArchiveGetUploadedFilesInfoFileListResult> fileInfo;

}
