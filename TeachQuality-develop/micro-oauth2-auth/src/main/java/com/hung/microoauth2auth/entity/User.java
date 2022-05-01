package com.hung.microoauth2auth.entity;

import lombok.Data;

@Data
public class User {
    private Integer id;

    private String userName;

    private String userPwd;

    private String realName;

    private String userType;

    private String userBelong;

    private Integer isClazz;

    private String email;
}