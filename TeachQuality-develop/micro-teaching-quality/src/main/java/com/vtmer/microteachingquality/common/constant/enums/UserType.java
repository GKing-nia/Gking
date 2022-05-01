package com.vtmer.microteachingquality.common.constant.enums;

import lombok.Getter;

/**
 * 账号用户身份
 */
@Getter
public enum UserType {

    NORMAL("0", "普通用户账号"),

    SCHOOL("1", "学校用户账号"),

    MANAGER("2", "专业负责人"),

    MASTER("3", "评审专家"),

    LEADER("4", "专家组长"),

    CLAZZ_PRINCIPAL("5", "课程负责人");

    private final String code;

    private final String type;

    UserType(String code, String type) {
        this.code = code;
        this.type = type;
    }

}

