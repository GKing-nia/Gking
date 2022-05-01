package com.vtmer.microteachingquality.model.dto.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author 墨小小
 * @date 21-09-25 16:34
 */
@Data
@ApiModel("专业归档负责人 获取负责的专业批次的文件信息")
public class MajorArchiveGetBatchFilesInfoResult {

    @ApiModelProperty("文件id")
    private Integer id;

    @ApiModelProperty("上传用户id")
    private Integer userId;

    @ApiModelProperty("上传用户名字")
    private String userName;

    @ApiModelProperty("文件名")
    private String fileName;

    @ApiModelProperty("文件路径")
    private String path;

    @ApiModelProperty("更新时间")
    private Date updateTime;

}
