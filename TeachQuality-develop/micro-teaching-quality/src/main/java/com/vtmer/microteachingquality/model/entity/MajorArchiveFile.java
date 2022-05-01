package com.vtmer.microteachingquality.model.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * major_archive_file
 *
 * @author
 */
@Data
public class MajorArchiveFile implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    /**
     * 上传该文件的用户id
     */
    private Integer userId;
    /**
     * 用户负责的专业id
     */
    private Integer majorId;
    /**
     * 批次名
     */
    private String batchName;
    /**
     * 文件名字
     */
    private String fileName;
    /**
     * 文件加密路径
     */
    private String path;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 创建时间
     */
    private Date createTime;
}