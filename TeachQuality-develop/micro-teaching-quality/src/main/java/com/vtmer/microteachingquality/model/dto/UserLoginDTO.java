package com.vtmer.microteachingquality.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel("用户登录传输对象")
public class UserLoginDTO {

    @ApiModelProperty(value = "用户名", required = true)
    @NotBlank(message = "用户名不能为空")
    private String userName;

    @ApiModelProperty(value = "登陆密码", required = true)
    @NotBlank(message = "登陆密码不能为空")
    private String userPwd;

}
