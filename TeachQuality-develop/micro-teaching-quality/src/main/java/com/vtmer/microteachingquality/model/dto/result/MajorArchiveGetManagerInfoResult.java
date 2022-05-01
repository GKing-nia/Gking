package com.vtmer.microteachingquality.model.dto.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 墨小小
 * @date 21-09-24 23:17
 */
@Data
@ApiModel("获取专业归档负责人信息")
public class MajorArchiveGetManagerInfoResult {

    @ApiModelProperty("用户id")
    private Integer id;

    @ApiModelProperty("真实姓名")
    private String realName;

    @ApiModelProperty("所属部门")
    private String userBelong;

    //用户类型就是 专业归档负责人
}
