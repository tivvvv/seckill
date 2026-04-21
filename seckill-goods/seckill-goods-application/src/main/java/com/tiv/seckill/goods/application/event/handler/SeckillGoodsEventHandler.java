package com.tiv.seckill.goods.application.event.handler;

import com.alibaba.cola.dto.Response;
import com.alibaba.cola.event.EventHandler;
import com.alibaba.cola.event.EventHandlerI;
import com.alibaba.fastjson.JSON;
import com.tiv.seckill.goods.application.cache.service.SeckillGoodsCacheService;
import com.tiv.seckill.goods.application.cache.service.SeckillGoodsListCacheService;
import com.tiv.seckill.goods.domain.event.SeckillGoodsEvent;
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
        log.info("SeckillGoodsEventHandler--execute--seckillGoodsEvent:{}", JSON.toJSONString(seckillGoodsEvent));
        if (seckillGoodsEvent.getId() == null) {
            return Response.buildSuccess();
        }
        seckillGoodsCacheService.tryUpdateSeckillGoodsCacheByLock(seckillGoodsEvent.getId());
        seckillGoodsListCacheService.tryUpdateSeckillGoodsListCacheByLock(seckillGoodsEvent.getActivityId());
        return Response.buildSuccess();
    }

}