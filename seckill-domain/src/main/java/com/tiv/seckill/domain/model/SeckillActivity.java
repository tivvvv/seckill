package com.tiv.seckill.domain.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 秒杀活动
 *
 * @TableName seckill_activity
 */
@Data
@TableName(value = "seckill_activity")
public class SeckillActivity implements Serializable {

    /**
     * 秒杀活动id
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 活动名称
     */
    @TableField(value = "activity_name")
    private String activityName;

    /**
     * 活动描述
     */
    @TableField(value = "activity_desc")
    private String activityDesc;

    /**
     * 活动状态 0:已发布 1:上线 2:下线
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

}