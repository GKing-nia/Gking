package com.vtmer.microteachingquality.model.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * clazz_evaluate_option
 *
 * @author
 */
@Data
public class ClazzEvaluateOption implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    /**
     * 课程类型
     */
    private String clazzType;
    /**
     * 一级指标
     */
    private String firstTarget;
    /**
     * 具体细节
     */
    private String detail;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
}