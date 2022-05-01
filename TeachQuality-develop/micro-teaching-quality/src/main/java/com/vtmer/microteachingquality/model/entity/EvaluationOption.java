package com.vtmer.microteachingquality.model.entity;

import java.util.Date;

public class EvaluationOption {
    private Integer id;

    private String collegeSort;

    private String firstTarget;

    private Date createTime;

    private Date updateTime;

    private String details;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCollegeSort() {
        return collegeSort;
    }

    public void setCollegeSort(String collegeSort) {
        this.collegeSort = collegeSort == null ? null : collegeSort.trim();
    }

    public String getFirstTarget() {
        return firstTarget;
    }

    public void setFirstTarget(String firstTarget) {
        this.firstTarget = firstTarget == null ? null : firstTarget.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details == null ? null : details.trim();
    }
}