package com.tiv.seckill.domain.event;

import com.tiv.seckill.domain.event.common.SeckillBaseEvent;

public class SeckillActivityEvent extends SeckillBaseEvent {

    public SeckillActivityEvent(Long id, Integer status) {
        super(id, status);
    }

}