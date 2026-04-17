package com.tiv.seckill.application.command;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class SeckillOrderCommand implements Serializable {

    /**
     * 商品id
     */
    private Long goodsId;

    /**
     * 活动id
     */
    private Long activityId;

    /**
     * 下单商品数量
     */
    private Integer quantity;

    /**
     * 商品版本号
     */
    private Long version;

    @Serial
    private static final long serialVersionUID = 1L;

}