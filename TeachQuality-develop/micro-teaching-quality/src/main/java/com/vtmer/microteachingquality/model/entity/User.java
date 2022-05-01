package com.vtmer.microteachingquality.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("用户类")
public class User extends Model<User> {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String userName;

    private String userPwd;

    private String realName;

    private String userType;

    private String userBelong;

    private Integer isClazz;

    private String email;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}