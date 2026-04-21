package com.tiv.seckill.order.domain.event;

import com.tiv.seckill.common.event.SeckillBaseEvent;

public class SeckillOrderEvent extends SeckillBaseEvent {

    public SeckillOrderEvent(Long id, Integer status) {
        super(id, status);
    }

}