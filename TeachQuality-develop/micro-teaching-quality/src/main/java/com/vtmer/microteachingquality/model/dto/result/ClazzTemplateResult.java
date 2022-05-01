package com.vtmer.microteachingquality.model.dto.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("课程自评报告模版信息传输对象")
public class ClazzTemplateResult {

    @ApiModelProperty(value = "课程自评报告模版id")
    private Integer id;

    @ApiModelProperty(value = "用户id")
    private Integer userId;

    @ApiModelProperty(value = "文件名")
    private String name;

    @ApiModelProperty(value = "学院")
    private String clazzCollege;

    @ApiModelProperty(value = "专业")
    private String clazzMajor;

    @ApiModelProperty(value = "课程类型")
    private String clazzType;

    @ApiModelProperty(value = "课程名字")
    private String clazzName;

    @ApiModelProperty(value = "模版文件路径")
    private String path;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

}
