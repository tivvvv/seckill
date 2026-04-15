package com.tiv.seckill.domain.event;

import com.tiv.seckill.domain.event.common.SeckillBaseEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SeckillGoodsEvent extends SeckillBaseEvent {

    private Long activityId;

    public SeckillGoodsEvent(Long id, Integer status, Long activityId) {
        super(id, status);
        this.activityId = activityId;
    }

}