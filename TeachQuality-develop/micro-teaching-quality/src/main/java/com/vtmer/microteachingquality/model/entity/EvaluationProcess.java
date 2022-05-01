package com.vtmer.microteachingquality.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Hung
 * @date 2022/4/19 22:13
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationProcess extends Model<EvaluationProcess> {
    @TableId(value = "evaluation_id", type = IdType.ASSIGN_ID)
    private Long id;
    @ApiModelProperty("创建此课程用户Id")
    private Integer creatorId;
    @ApiModelProperty("此评审所属课程Id")
    private Integer clazzId;
    @ApiModelProperty("当前评审阶段，1代表创建流程完成，2代表提交评审材料，3代表专家评审，4代表专家组长评审，5代表 专家小组给出评审意见，6代表流程结束")
    private String currentPhases;
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    public EvaluationProcess(Integer creatorId, Integer clazzId, String currentPhases) {
        this.creatorId = creatorId;
        this.clazzId = clazzId;
        this.currentPhases = currentPhases;
    }
}
