package com.vtmer.microteachingquality.model.dto.insert;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @author : Gking
 * @date : 2022-04-23 10:17
 **/
@Data
@ApiModel("创建账号传输对象")
@AllArgsConstructor
@NoArgsConstructor
public class UserInsertDTO {
    @ApiModelProperty(value = "账号名称", required = true)
    @NotBlank(message = "账号名称不能为空")
    private String userName;

    @ApiModelProperty(value = "账号密码", required = true)
    @NotBlank(message = "账号密码不能为空")
    @Pattern(regexp = "^(?=.*[0-9].*)(?=.*[A-Z].*)(?=.*[a-z].*).{8,20}$", message = "密码必须包含数字、小写字母、大写字母，限制长度为8-20位")
    private String userPwd;

    @ApiModelProperty(value = "密码确认", required = true)
    @NotBlank(message = "密码确认不能为空")
    @Pattern(regexp = "^(?=.*[0-9].*)(?=.*[A-Z].*)(?=.*[a-z].*).{8,20}$", message = "密码必须包含数字、小写字母、大写字母，限制长度为8-20位")
    private String confirmPassword;

    @ApiModelProperty(value = "真实姓名", required = true)
    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    @ApiModelProperty(value = "账号类型", required = true)
    @NotBlank(message = "账号类型不能为空")
    private String userType;

    @ApiModelProperty(value = "所属部门", required = true)
    @NotBlank(message = "所属部门或指定专业不能为空")
    private String userBelong;

    @ApiModelProperty(value = "是否为课程评价系统用户", required = true)
    @NotBlank(message = "是否为课程评价不能为空")
    private Integer isClazz;

    @ApiModelProperty(value = "qq邮箱", required = true)
    @NotBlank(message = "邮箱不能为空")
    private String email;

    @ApiModelProperty(value = "验证码", required = true)
    @NotBlank(message = "验证码不能为空")
    private String code;
}
