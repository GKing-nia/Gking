package com.vtmer.microteachingquality.model.dto.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author 墨小小
 * @date 21-09-24 16:24
 */
@Data
@ApiModel("专业归档 获取模板文件信息 结果集")
public class MajorArchiveGetTemplateFilesInfoResult {

    @ApiModelProperty("文件id")
    private Integer id;

    @ApiModelProperty("文件名字")
    private String fileName;

    @ApiModelProperty("文件路径")
    private String filePath;

    @ApiModelProperty("更新时间")
    private Date updateTime;
}
