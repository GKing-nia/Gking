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
 * clazz_opinion_record
 *
 * @author
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel
@AllArgsConstructor
@NoArgsConstructor
public class ClazzOpinionRecord extends Model<ClazzOpinionRecord> implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 课程id
     */
    private Integer clazzId;
    @ApiModelProperty("填写意见的用户id")
    private Integer userId;
    @ApiModelProperty("专家意见")
    private String opinion;
    @ApiModelProperty("课程优点")
    private String clazzAdvantage;
    @ApiModelProperty("课程问题")
    private String clazzProblem;
    @ApiModelProperty("课程建议")
    private String clazzAdvice;
    @ApiModelProperty("评审流程Id")
    private Long evaluationId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    public ClazzOpinionRecord(Integer userId, String clazzAdvantage, String clazzProblem, String clazzAdvice, Long evaluationId) {
        this.userId = userId;
        this.clazzAdvantage = clazzAdvantage;
        this.clazzProblem = clazzProblem;
        this.clazzAdvice = clazzAdvice;
        this.evaluationId = evaluationId;
    }
}