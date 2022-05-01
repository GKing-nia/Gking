package com.vtmer.microteachingquality.model.dto.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("获取账号信息传输对象")
public class UserInfoResult {

    @ApiModelProperty(value = "账号id")
    private Integer id;

    @ApiModelProperty(value = "账号名称")
    private String userName;

    @ApiModelProperty(value = "真实姓名")
    private String realName;

    @ApiModelProperty(value = "账号类型")
    private String userType;

    @ApiModelProperty(value = "所属部门或指定专业")
    private String userBelong;

    @ApiModelProperty(value = "账号创建时间")
    private Date createTime;

}
