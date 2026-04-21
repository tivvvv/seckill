package com.tiv.seckill.order.application.event.handler;

import com.alibaba.cola.dto.Response;
import com.alibaba.cola.event.EventHandler;
import com.alibaba.cola.event.EventHandlerI;
import com.alibaba.fastjson.JSON;
import com.tiv.seckill.order.domain.event.SeckillOrderEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EventHandler
public class SeckillOrderEventHandler implements EventHandlerI<Response, SeckillOrderEvent> {

    @Override
    public Response execute(SeckillOrderEvent seckillOrderEvent) {
        log.info("SeckillOrderEventHandler--execute--seckillOrderEvent:{}", JSON.toJSONString(seckillOrderEvent));
        if (seckillOrderEvent.getId() == null) {
            log.error("orderEvent|订单参数错误");
            return Response.buildSuccess();
        }
        return Response.buildSuccess();
    }

}