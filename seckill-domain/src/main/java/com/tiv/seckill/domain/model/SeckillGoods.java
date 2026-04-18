package com.tiv.seckill.domain.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 秒杀商品表
 *
 * @TableName seckill_goods
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName(value = "seckill_goods")
public class SeckillGoods implements Serializable {

    /**
     * 商品id
     */
    @TableId(value = "id")
    private Long id;

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
     * 商品描述
     */
    @TableField(value = "goods_desc")
    private String goodsDesc;

    /**
     * 商品图片
     */
    @TableField(value = "goods_img")
    private String goodsImg;

    /**
     * 商品原价格
     */
    @TableField(value = "original_price")
    private BigDecimal originalPrice;

    /**
     * 商品秒杀价格
     */
    @TableField(value = "activity_price")
    private BigDecimal activityPrice;

    /**
     * 商品初始库存
     */
    @TableField(value = "initial_stock")
    private Integer initialStock;

    /**
     * 商品当前可用库存
     */
    @TableField(value = "available_stock")
    private Integer availableStock;

    /**
     * 限购个数
     */
    @TableField(value = "limit_num")
    private Integer limitNum;

    /**
     * 商品状态 0:已发布 1:上线 2:下线
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 开始时间
     */
    @TableField(value = "start_time")
    private Date startTime;

    /**
     * 结束时间
     */
    @TableField(value = "end_time")
    private Date endTime;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    public boolean validateParams() {
        return !StringUtils.isEmpty(goodsName)
                && activityId != null
                && startTime != null
                && endTime != null
                && !startTime.after(endTime)
                && !endTime.before(new Date())
                && activityPrice != null
                && activityPrice.compareTo(BigDecimal.ZERO) >= 0
                && originalPrice != null
                && originalPrice.compareTo(BigDecimal.ZERO) >= 0
                && initialStock != null
                && initialStock > 0
                && limitNum != null
                && limitNum > 0;
    }

}