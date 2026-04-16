package com.tiv.seckill.domain.event;

import com.tiv.seckill.domain.event.common.SeckillBaseEvent;

public class SeckillOrderEvent extends SeckillBaseEvent {

    public SeckillOrderEvent(Long id, Integer status) {
        super(id, status);
    }

}