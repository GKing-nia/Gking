package com.vtmer.microteachingquality.model.dto.select;

import com.vtmer.microteachingquality.model.dto.result.GetAllClazzTemplateResult;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("分页返回课程评价模板")
public class GetAllClazzTemplateDTO {

    @ApiModelProperty("课程自评模板集合")
    List<GetAllClazzTemplateResult> list;

    @ApiModelProperty("总条数")
    Integer size;

}
