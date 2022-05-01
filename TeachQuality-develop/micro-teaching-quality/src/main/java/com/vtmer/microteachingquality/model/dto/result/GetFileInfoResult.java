package com.vtmer.microteachingquality.model.dto.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("根据专业名称获取对应自评报告文件信息")
public class GetFileInfoResult {

    @ApiModelProperty("文件id")
    private Integer reportId;

    @ApiModelProperty("上传者id")
    private Integer uploaderId;

    @ApiModelProperty("上传者真实姓名")
    private String uploaderRealName;

    @ApiModelProperty("上传者所属机构/专业")
    private String upLoaderBelongs;

    @ApiModelProperty("文件路径")
    private String filePath;

    @ApiModelProperty("更新时间")
    private Date updateTime;

}
