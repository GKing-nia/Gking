package com.vtmer.microteachingquality.model.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * manage_info
 *
 * @author
 */
@Data
public class ManageInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    /**
     * 评审人员id
     */
    private Integer userId;
    /**
     * 被评审专业id
     */
    private Integer majorId;
    /**
     * 冗余评审状态
     */
    private String status;
}