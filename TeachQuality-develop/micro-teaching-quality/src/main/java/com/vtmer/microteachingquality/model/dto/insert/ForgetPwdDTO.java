package com.vtmer.microteachingquality.model.dto.insert;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

@ApiModel("忘记密码传输对象")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgetPwdDTO {

    @ApiModelProperty("用户帐号")
    private String userAccount;

    @ApiModelProperty("用户真实姓名")
    private String name;

    @ApiModelProperty("用户邮箱")
    private String email;

    @ApiModelProperty("重置的密码")
    @Pattern(regexp = "^(?=.*[0-9].*)(?=.*[A-Z].*)(?=.*[a-z].*).{8,20}$", message = "密码必须包含数字、小写字母、大写字母，限制长度为8-20位")
    private String password;

    @ApiModelProperty("密码确认")
    @Pattern(regexp = "^(?=.*[0-9].*)(?=.*[A-Z].*)(?=.*[a-z].*).{8,20}$", message = "密码必须包含数字、小写字母、大写字母，限制长度为8-20位")
    private String confirmPassword;

    @ApiModelProperty("验证码")
    private String code;

}
