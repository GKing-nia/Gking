package com.vtmer.microteachingquality.model.entity;

import java.io.Serializable;

/**
 * clazz_expert_manage_info
 *
 * @author
 */
public class ClazzExpertManageInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    /**
     * 课程评审专家的id
     */
    private Integer userId;
    /**
     * 要管理的课程id
     */
    private Integer clazzId;
    /**
     * 评审状态：0未评审，1已经评审
     */
    private String status;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getClazzId() {
        return clazzId;
    }

    public void setClazzId(Integer clazzId) {
        this.clazzId = clazzId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}