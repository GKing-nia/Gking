package com.vtmer.microteachingquality.model.dto.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("专业与类型")
@Data
public class GetAllMajorsAndTypesResult {

    @ApiModelProperty("专业类型")
    private String majorType;

    @ApiModelProperty("该专业类型对应拥有的专业")
    private List<GetAllMajorsResult> majors;
}
