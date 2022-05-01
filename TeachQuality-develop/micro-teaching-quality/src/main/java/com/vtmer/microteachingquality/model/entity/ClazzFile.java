package com.vtmer.microteachingquality.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * clazz_file
 *
 * @author
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class ClazzFile extends Model<ClazzFile> implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Integer id;
    @ApiModelProperty("上传该文件的用户")
    private Integer userId;
    @ApiModelProperty("文件名字")
    private String fileName;
    @ApiModelProperty("用户所属的学院")
    private String clazzCollege;
    @ApiModelProperty("用户所属的专业")
    private String clazzMajor;
    @ApiModelProperty("用户所属专业的类型")
    private String clazzType;
    @ApiModelProperty("用户负责的课程名字")
    private String clazzName;
    @ApiModelProperty("所属课程id")
    private Integer clazzId;
    @ApiModelProperty("所属评审流程id")
    private Long evaluationId;
    @ApiModelProperty("文件所在路径")
    private String path;
    @ApiModelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    public ClazzFile(Integer userId, String fileName, String clazzCollege, String clazzMajor, String clazzType, String clazzName, String path) {
        this.userId = userId;
        this.fileName = fileName;
        this.clazzCollege = clazzCollege;
        this.clazzMajor = clazzMajor;
        this.clazzType = clazzType;
        this.clazzName = clazzName;
        this.path = path;
    }

    public ClazzFile(Integer userId, String fileName, Integer clazzId, Long evaluationId, String path) {
        this.userId = userId;
        this.fileName = fileName;
        this.clazzId = clazzId;
        this.evaluationId = evaluationId;
        this.path = path;
    }
}