package com.vtmer.microteachingquality.model.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * major_archive_opinion
 *
 * @author
 */
@Data
public class MajorArchiveOpinion implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    /**
     * 被评审的专业id
     */
    private Integer majorId;
    /**
     * 提交评审的用户id
     */
    private Integer userId;
    /**
     * 被评审的批次名字
     */
    private String batchName;
    /**
     * 专家意见
     */
    private String opinion;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
}