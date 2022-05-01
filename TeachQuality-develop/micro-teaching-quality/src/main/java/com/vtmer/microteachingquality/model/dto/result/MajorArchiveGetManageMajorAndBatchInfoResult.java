package com.vtmer.microteachingquality.model.dto.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 墨小小
 * @date 21-09-25 16:10
 */
@Data
@ApiModel("专业归档负责人  获取管理的专业与审批批次，评审状态")
public class MajorArchiveGetManageMajorAndBatchInfoResult {

    @ApiModelProperty("专业id")
    private Integer majorId;

    @ApiModelProperty("专业名字")
    private String majorName;

    @ApiModelProperty("评审批次List")
    private List<MajorArchiveGetManageMajorAndBatchInfoBatchListResult> batchList;


}
