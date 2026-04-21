package com.tiv.seckill.goods.application.cache.service;

import com.tiv.seckill.common.cache.model.SeckillBusinessCache;
import com.tiv.seckill.common.cache.service.SeckillCacheService;
import com.tiv.seckill.goods.domain.model.SeckillGoods;

public interface SeckillGoodsCacheService extends SeckillCacheService {

    SeckillBusinessCache<SeckillGoods> getCachedGoods(Long goodsId, Long version);

    SeckillBusinessCache<SeckillGoods> tryUpdateSeckillGoodsCacheByLock(Long goodsId);

}