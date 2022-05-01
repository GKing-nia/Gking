package com.vtmer.microteachingquality.model.dto.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@ApiModel("根据课程名字，获取所有评审文件")
@AllArgsConstructor
public class GetClazzFilesResult {

    @ApiModelProperty("文件id")
    private Integer id;

    @ApiModelProperty("上传用户id")
    private Integer userId;

    @ApiModelProperty("课程id")
    private Integer clazzId;

    @ApiModelProperty("文件名字")
    private String fileName;

    @ApiModelProperty("文件路径")
    private String path;

}
