package com.tiv.seckill.application.event.handler;

import com.alibaba.cola.dto.Response;
import com.alibaba.cola.event.EventHandler;
import com.alibaba.cola.event.EventHandlerI;
import com.tiv.seckill.application.cache.service.goods.SeckillGoodsCacheService;
import com.tiv.seckill.application.cache.service.goods.SeckillGoodsListCacheService;
import com.tiv.seckill.domain.event.SeckillGoodsEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@EventHandler
public class SeckillGoodsEventHandler implements EventHandlerI<Response, SeckillGoodsEvent> {

    @Autowired
    private SeckillGoodsCacheService seckillGoodsCacheService;

    @Autowired
    private SeckillGoodsListCacheService seckillGoodsListCacheService;

    @Override
    public Response execute(SeckillGoodsEvent seckillGoodsEvent) {
        log.info("SeckillGoodsEventHandler--execute--seckillGoodsEvent:{}", seckillGoodsEvent);
        if (seckillGoodsEvent.getId() == null) {
            return Response.buildSuccess();
        }
        seckillGoodsCacheService.tryUpdateSeckillGoodsCacheByLock(seckillGoodsEvent.getId());
        seckillGoodsListCacheService.tryUpdateSeckillGoodsListCacheByLock(seckillGoodsEvent.getActivityId());
        return Response.buildSuccess();
    }

}