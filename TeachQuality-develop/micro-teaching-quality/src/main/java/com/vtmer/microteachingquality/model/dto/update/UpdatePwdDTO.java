package com.vtmer.microteachingquality.model.dto.update;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@ApiModel("修改密码传输对象")
public class UpdatePwdDTO {

    @ApiModelProperty(value = "原始密码", required = true)
    @NotBlank(message = "原始密码不能为空")
    private String oldPwd;

    @ApiModelProperty(value = "新密码(密码必须包含数字、小写字母、大写字母，限制长度为8-20位)", required = true)
    @Pattern(regexp = "^(?=.*[0-9].*)(?=.*[A-Z].*)(?=.*[a-z].*).{8,20}$", message = "密码必须包含数字、小写字母、大写字母，限制长度为8-20位")
    private String newPwd;

    @ApiModelProperty(value = "重复密码", required = true)
    @NotBlank(message = "请再次输入新密码")
    private String newPwdConfirm;

}
