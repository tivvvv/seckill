package com.tiv.seckill.domain.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 秒杀订单 DTO
 */
@Data
public class SeckillOrderDTO implements Serializable {

    /**
     * 订单id
     */
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

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

    @Serial
    private static final long serialVersionUID = 1L;

}