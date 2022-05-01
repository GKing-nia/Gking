package com.vtmer.microteachingquality.model.dto.select;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("分页获取全部课程评价模板请求DTO")
public class ClazzTemplateDTO {

    @ApiModelProperty(value = "开始的下标", hidden = true)
    private Integer start = 0;

    @NotNull(message = "每页个数不能为空")
    @ApiModelProperty(value = "每页个数", required = true)
    private Integer size;

    @NotNull(message = "页面不能为空")
    @ApiModelProperty(value = "页码", required = true)
    private Integer page = 1;


    public int getStart() {
        if (size == 0) {
            size = 100;
        }
        if (page <= 0) {
            page = 1;
        }
        start = (page - 1) * size;
        return start;
    }

}
