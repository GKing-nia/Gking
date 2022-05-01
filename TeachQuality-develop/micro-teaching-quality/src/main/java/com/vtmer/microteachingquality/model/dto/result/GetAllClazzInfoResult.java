package com.vtmer.microteachingquality.model.dto.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@ApiModel("获取所有课程评价的相关信息：文件path，评审状态，课程信息等")
@Data
@AllArgsConstructor
public class GetAllClazzInfoResult {

    @ApiModelProperty("课程名字")
    private String clazzName;

    @ApiModelProperty("课程id")
    private Integer clazzId;

    @ApiModelProperty("课程负责人提交的文件列表")
    private List<FileInfoResult> fileInfoList;
}
