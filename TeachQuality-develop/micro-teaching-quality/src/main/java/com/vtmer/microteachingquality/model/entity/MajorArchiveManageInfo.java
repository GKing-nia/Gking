package com.vtmer.microteachingquality.model.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * major_archive_manage_info
 *
 * @author
 */
@Data
public class MajorArchiveManageInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    /**
     * 管理者id
     */
    private Integer userId;
    /**
     * 被管理的专业id
     */
    private Integer majorId;
    /**
     * 批次名字
     */
    private String batchName;
    /**
     * 该批次的评审状态：1已经评审，0未评审
     */
    private String status;
}