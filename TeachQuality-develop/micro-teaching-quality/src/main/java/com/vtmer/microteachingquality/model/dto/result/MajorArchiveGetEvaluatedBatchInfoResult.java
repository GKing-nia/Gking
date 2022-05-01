package com.vtmer.microteachingquality.model.dto.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author 墨小小
 * @date 21-09-25 17:13
 */
@Data
@ApiModel("根据专业和批次获取已经评审的信息opinion")
public class MajorArchiveGetEvaluatedBatchInfoResult {

    @ApiModelProperty("意见记录id")
    private Integer id;

    @ApiModelProperty("意见")
    private String opinion;

    @ApiModelProperty("更新时间")
    private Date updateTime;

}
