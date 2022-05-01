package com.hung.microoauth2auth.entity;

import lombok.Data;

/**
 * 单个权限
 *
 * @author Hung
 * @version 1.0
 **/
@Data
public class PermissionDTO {
    private String id;
    /**
     * 权限标识
     */
    private String authority;
    /**
     * 描述
     */
    private String description;
    /**
     * 访问地址
     */
    private String url;
}
