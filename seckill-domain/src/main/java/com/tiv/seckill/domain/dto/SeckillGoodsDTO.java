package com.tiv.seckill.domain.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 秒杀商品 DTO
 */
@Data
public class SeckillGoodsDTO implements Serializable {

    /**
     * 商品id
     */
    private Long id;

    /**
     * 活动id
     */
    private Long activityId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品描述
     */
    private String goodsDesc;

    /**
     * 商品图片
     */
    private String goodsImg;

    /**
     * 商品原价格
     */
    private BigDecimal originalPrice;

    /**
     * 商品秒杀价格
     */
    private BigDecimal activityPrice;

    /**
     * 商品初始库存
     */
    private Integer initialStock;

    /**
     * 商品当前可用库存
     */
    private Integer availableStock;

    /**
     * 限购个数
     */
    private Integer limitNum;

    /**
     * 商品状态 0:已发布 1:上线 2:下线
     */
    private Integer status;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 版本号
     */
    private Long version;

    @Serial
    private static final long serialVersionUID = 1L;

}