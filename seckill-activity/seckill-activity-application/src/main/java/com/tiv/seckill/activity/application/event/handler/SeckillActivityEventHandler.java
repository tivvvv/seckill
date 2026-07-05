package com.tiv.seckill.activity.application.event.handler;

import com.alibaba.cola.dto.Response;
import com.alibaba.cola.event.EventHandler;
import com.alibaba.cola.event.EventHandlerI;
import com.alibaba.fastjson2.JSON;
import com.tiv.seckill.activity.application.cache.service.SeckillActivityCacheService;
import com.tiv.seckill.activity.application.cache.service.SeckillActivityListCacheService;
import com.tiv.seckill.activity.domain.event.SeckillActivityEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@EventHandler
public class SeckillActivityEventHandler implements EventHandlerI<Response, SeckillActivityEvent> {

    @Autowired
    private SeckillActivityCacheService seckillActivityCacheService;

    @Autowired
    private SeckillActivityListCacheService seckillActivityListCacheService;

    @Override
    public Response execute(SeckillActivityEvent seckillActivityEvent) {
        log.info("SeckillActivityEventHandler--execute--seckillActivityEvent:{}", JSON.toJSONString(seckillActivityEvent));
        if (seckillActivityEvent == null) {
            return Response.buildSuccess();
        }
        seckillActivityCacheService.tryUpdateSeckillActivityCacheByLock(seckillActivityEvent.getId());
        seckillActivityListCacheService.tryUpdateSeckillActivityListCacheByLock(seckillActivityEvent.getStatus());
        return Response.buildSuccess();
    }

}