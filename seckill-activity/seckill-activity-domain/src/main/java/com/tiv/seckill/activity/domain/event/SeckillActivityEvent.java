package com.tiv.seckill.activity.domain.event;

import com.tiv.seckill.common.event.SeckillBaseEvent;

public class SeckillActivityEvent extends SeckillBaseEvent {

    public SeckillActivityEvent(Long id, Integer status) {
        super(id, status);
    }

}