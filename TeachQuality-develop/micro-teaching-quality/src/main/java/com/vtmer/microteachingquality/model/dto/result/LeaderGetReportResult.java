package com.vtmer.microteachingquality.model.dto.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("专业专家 组长 获取报告")
public class LeaderGetReportResult {
    @ApiModelProperty("报告id")
    private Integer id;
    @ApiModelProperty("上传用户id")
    private Integer userId;
    @ApiModelProperty("所属学院")
    private String college;
    @ApiModelProperty("所属专业")
    private String major;
    @ApiModelProperty("评审状态")
    private String status;
    @ApiModelProperty("文件名字")
    private String fileName;
    @ApiModelProperty("文件加密路径")
    private String path;
    @ApiModelProperty("创建时间")
    private Date createTime;
    @ApiModelProperty("更新时间")
    private Date updateTime;
}
