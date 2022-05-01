package com.vtmer.microteachingquality.model.dto.update;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class ForgetPwdDTO {

    @ApiModelProperty("用户帐号")
    private String userId;

    @ApiModelProperty("用户真实姓名")
    private String name;

    @ApiModelProperty("用户所属部门")
    private String userBelong;

}
