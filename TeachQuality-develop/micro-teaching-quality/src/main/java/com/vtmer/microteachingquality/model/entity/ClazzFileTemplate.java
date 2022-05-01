package com.vtmer.microteachingquality.model.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * clazz_file_template
 *
 * @author
 */
@Data
public class ClazzFileTemplate implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    /**
     * 上传该模板的用户id
     */
    private Integer userId;
    /**
     * 文件名字
     */
    private String name;
    /**
     * 模板所属的课程的学院
     */
    private String clazzCollege;
    /**
     * 模板所属课程的专业
     */
    private String clazzMajor;
    /**
     * 课程的类型
     */
    private String clazzType;
    /**
     * 课程的名字
     */
    private String clazzName;
    /**
     * 模板文件路径
     */
    private String path;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
}