package com.tiv.seckill.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 秒杀订单状态枚举
 */
@Getter
@AllArgsConstructor
public enum SeckillOrderStatusEnum {

    DELETED(-1, "已删除"),

    CANCELED(0, "已取消"),

    CREATED(1, "已创建"),

    PAID(2, "已支付");

    private final Integer code;

    private final String desc;

    public static boolean isCanceled(Integer status) {
        return CANCELED.getCode().equals(status);
    }

    public static boolean isDeleted(Integer status) {
        return DELETED.getCode().equals(status);
    }

}