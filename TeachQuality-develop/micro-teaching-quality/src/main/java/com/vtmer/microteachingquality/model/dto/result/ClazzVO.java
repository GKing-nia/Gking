package com.vtmer.microteachingquality.model.dto.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Hung
 * @date 2022/4/22 23:21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClazzVO {

    private Integer id;
    @ApiModelProperty("课程所属专业的学院")
    private String college;
    @ApiModelProperty("课程所属专业")
    private String major;
    @ApiModelProperty("课程名")
    private String name;
    @ApiModelProperty("课程年级")
    private String grade;
    @ApiModelProperty("创建课程的用户信息")
    private UserClazzVO userClazzVO;
    @ApiModelProperty("课程唯一的序列号")
    private String clazzSerialNumber;
    @ApiModelProperty("课程类型")
    private String type;
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

}
