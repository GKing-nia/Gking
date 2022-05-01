package com.vtmer.microteachingquality.model.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * major_archive_template_file
 *
 * @author
 */
@Data
public class MajorArchiveTemplateFile implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    /**
     * 上传该文件的用户id
     */
    private Integer userId;
    /**
     * 模板文件名字
     */
    private String fileName;
    /**
     * 模板文件路径
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