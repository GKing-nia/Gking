package com.vtmer.microteachingquality.common.constant.enums;

import lombok.Getter;

@Getter
public enum EvaluationStatus {
    NOT_CHECKED("0", "未评审"),

    CHECKED("1", "已评审"),

    WAITING_FILL("2", "未完成填写");

    private final String code;

    private final String status;

    EvaluationStatus(String code, String status) {
        this.code = code;
        this.status = status;
    }
}
