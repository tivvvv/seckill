package com.tiv.seckill.common.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 秒杀活动状态枚举
 */
@Getter
@AllArgsConstructor
public enum SeckillActivityStatusEnum {

    PUBLISHED(0, "已发布"),

    ONLINE(1, "上线"),

    OFFLINE(2, "下线");

    private final Integer code;

    private final String desc;

    public static boolean isOnline(Integer status) {
        return ONLINE.getCode().equals(status);
    }

    public static boolean isOffline(Integer status) {
        return OFFLINE.getCode().equals(status);
    }

}