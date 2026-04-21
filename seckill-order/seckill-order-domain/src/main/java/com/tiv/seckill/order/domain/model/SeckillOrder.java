package com.tiv.seckill.order.domain.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 秒杀订单表
 *
 * @TableName seckill_order
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName(value = "seckill_order")
public class SeckillOrder implements Serializable {

    /**
     * 订单id
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 用户id
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 商品id
     */
    @TableField(value = "goods_id")
    private Long goodsId;

    /**
     * 活动id
     */
    @TableField(value = "activity_id")
    private Long activityId;

    /**
     * 商品名称
     */
    @TableField(value = "goods_name")
    private String goodsName;

    /**
     * 秒杀价格
     */
    @TableField(value = "activity_price")
    private BigDecimal activityPrice;

    /**
     * 下单商品数量
     */
    @TableField(value = "quantity")
    private Integer quantity;

    /**
     * 订单总金额
     */
    @TableField(value = "order_price")
    private BigDecimal orderPrice;

    /**
     * 订单状态 -1:已删除 0:已取消 1:已创建 2:已支付
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}