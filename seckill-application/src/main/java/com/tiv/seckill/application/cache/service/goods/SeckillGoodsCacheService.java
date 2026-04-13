package com.tiv.seckill.application.cache.service.goods;

import com.tiv.seckill.application.cache.model.SeckillBusinessCache;
import com.tiv.seckill.application.cache.service.common.SeckillCacheService;
import com.tiv.seckill.domain.model.SeckillGoods;

public interface SeckillGoodsCacheService extends SeckillCacheService {

    SeckillBusinessCache<SeckillGoods> getCachedGoods(Long goodsId, Long version);

    SeckillBusinessCache<SeckillGoods> tryUpdateSeckillGoodsCacheByLock(Long goodsId);

}