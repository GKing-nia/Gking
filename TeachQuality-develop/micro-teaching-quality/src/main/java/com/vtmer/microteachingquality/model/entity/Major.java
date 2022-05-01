package com.vtmer.microteachingquality.model.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * major
 *
 * @author
 */
@ApiModel(value = "专业记录表，记录专业类型与名字")
public class Major implements Serializable {
    private Integer id;

    /**
     * 专业类型
     */
    @ApiModelProperty(value = "专业类型")
    private String type;

    /**
     * 专业名称
     */
    @ApiModelProperty(value = "专业名称")
    private String name;

    /**
     * 专业所属的学院
     */
    @ApiModelProperty(value = "专业所属的学院")
    private String college;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }
}