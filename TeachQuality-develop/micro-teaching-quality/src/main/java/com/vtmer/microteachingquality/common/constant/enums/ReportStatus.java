package com.vtmer.microteachingquality.common.constant.enums;

import lombok.Getter;

/**
 * 报告状态
 */
@Getter
public enum ReportStatus {

    NOT_FILL("0", "未完成填写"),

    WAITING_EVALUATE("1", "未评审"),

    FAIL("2", "评审不及格"),

    FINISHED("3", "已评审");

    private final String code;

    private final String status;

    ReportStatus(String code, String status) {
        this.code = code;
        this.status = status;
    }
}
