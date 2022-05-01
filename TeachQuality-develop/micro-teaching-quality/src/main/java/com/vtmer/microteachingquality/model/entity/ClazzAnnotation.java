package com.vtmer.microteachingquality.model.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * clazz_annotation
 *
 * @author
 */
@Data
public class ClazzAnnotation implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    /**
     * 记录需要提交的材料的信息
     */
    private String info;
    /**
     * 课程负责人提交材料页面的提示
     */
    private String annotation;
}