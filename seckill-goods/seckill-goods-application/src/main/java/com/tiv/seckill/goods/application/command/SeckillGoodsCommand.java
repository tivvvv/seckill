package com.tiv.seckill.goods.application.command;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class SeckillGoodsCommand implements Serializable {

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
     * 限购个数
     */
    private Integer limitNum;

    /**
     * 秒杀活动版本号
     */
    private Long activityVersion = 1L;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}