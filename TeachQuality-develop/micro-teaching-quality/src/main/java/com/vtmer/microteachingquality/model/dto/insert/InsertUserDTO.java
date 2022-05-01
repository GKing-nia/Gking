package com.vtmer.microteachingquality.model.dto.insert;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel("创建账号传输对象")
@AllArgsConstructor
@NoArgsConstructor
public class InsertUserDTO {

    @ApiModelProperty(value = "账号名称", required = true)
    @NotBlank(message = "账号名称不能为空")
    private String userName;

    // @ApiModelProperty(value = "账号密码", required = true)
    // @NotBlank(message = "账号密码不能为空")
    // private String userPwd;

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


}
