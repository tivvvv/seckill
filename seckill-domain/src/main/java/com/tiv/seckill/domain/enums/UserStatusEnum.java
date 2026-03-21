package com.tiv.seckill.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户状态枚举
 */
@Getter
@AllArgsConstructor
public enum UserStatusEnum {

    NORMAL(1, "正常"),

    FROZEN(2, "冻结");

    private final Integer code;

    private final String desc;

    public static boolean isNormal(Integer status) {
        return NORMAL.getCode().equals(status);
    }

}