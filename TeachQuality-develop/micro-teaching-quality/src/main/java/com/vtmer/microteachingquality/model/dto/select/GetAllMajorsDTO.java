package com.vtmer.microteachingquality.model.dto.select;

import com.vtmer.microteachingquality.model.dto.result.GetAllMajorsResult;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("分页获取所有专业和总数目数")
public class GetAllMajorsDTO {

    @ApiModelProperty("专业集合")
    private List<GetAllMajorsResult> majorsList;

    @ApiModelProperty("总条数")
    private Integer size;
}
