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

import java.time.LocalDateTime;

/**
 * @author Hung
 * @date 2022/4/19 22:23
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel("专家小组的评审意见")
@Data
@NoArgsConstructor
public class ClazzOpinionLeaderRecord extends Model<ClazzOpinionLeaderRecord> {

    @TableId(type = IdType.AUTO)
    private Integer id;
    @ApiModelProperty("评审流程Id")
    private Long evaluationId;
    @ApiModelProperty("课程Id")
    private Integer clazzId;
    @ApiModelProperty("专家评审Id")
    private Integer userId;
    @ApiModelProperty("专家小组意见")
    @TableField(value = "opinion")
    private String evaluationOpinion;
    @ApiModelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @ApiModelProperty("更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
