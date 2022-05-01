package com.vtmer.microteachingquality.model.dto.insert;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Hung
 * @date 2022/4/20 17:02
 */
@ApiModel("创建课程输入类")
@Data
public class ClazzBo {
    @ApiModelProperty("学院")
    private String college;
    @ApiModelProperty("专业")
    private String major;
    @ApiModelProperty("年级")
    private String grade;
    @ApiModelProperty("课程类型")
    private String type;
    @ApiModelProperty("课程名称")
    private String name;
    @ApiModelProperty("课程唯一序列号")
    private String clazzSerialNumber;

}
