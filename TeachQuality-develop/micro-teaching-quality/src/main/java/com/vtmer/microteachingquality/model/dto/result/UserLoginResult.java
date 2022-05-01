package com.vtmer.microteachingquality.model.dto.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("用户登陆信息传输对象")
public class UserLoginResult {

    @ApiModelProperty(value = "用户id")
    private Integer userId;

    @ApiModelProperty(value = "真实姓名")
    private String realName;

    @ApiModelProperty(value = "用户类型")
    private String userType;

    @ApiModelProperty(value = "专业/部门")
    private String userBelong;

}
