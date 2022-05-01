package com.vtmer.microteachingquality.common.constant.enums;

public enum ClazzStatusEnums {
    NOT_EVALUATE(0, "未评审"),
    EVALUATED(1, "已经评审");
    private final Integer code;
    private final String desc;

    ClazzStatusEnums(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
