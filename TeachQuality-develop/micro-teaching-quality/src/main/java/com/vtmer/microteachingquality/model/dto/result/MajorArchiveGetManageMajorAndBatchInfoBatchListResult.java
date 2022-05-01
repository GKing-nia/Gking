package com.vtmer.microteachingquality.model.dto.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 墨小小
 * @date 21-09-29 16:54
 */
@Data
@ApiModel("评审批次List")
public class MajorArchiveGetManageMajorAndBatchInfoBatchListResult {

    @ApiModelProperty("评审批次名字")
    private String batchName;

    @ApiModelProperty("评审状态：0未评审，1已评审")
    private String status;

}
