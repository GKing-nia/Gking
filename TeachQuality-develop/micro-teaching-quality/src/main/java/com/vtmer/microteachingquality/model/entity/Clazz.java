package com.vtmer.microteachingquality.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * clazz
 *
 * @author
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@ApiModel
public class Clazz extends Model<Clazz> implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Integer id;
    @ApiModelProperty("课程所属专业的学院")
    private String college;
    @ApiModelProperty("课程所属专业")
    private String major;
    @ApiModelProperty("课程名")
    private String name;
    @ApiModelProperty("课程年级")
    private String grade;
    @ApiModelProperty("创建课程的用户ID")
    private Integer userId;
    @ApiModelProperty("课程唯一的序列号")
    private String clazzSerialNumber;
    @ApiModelProperty("课程类型")
    private String type;
    @ApiModelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @ApiModelProperty("更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    public Clazz(String college, String major, String name, String type) {
        this.college = college;
        this.major = major;
        this.name = name;
        this.type = type;
    }
}