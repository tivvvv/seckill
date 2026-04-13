package com.tiv.seckill.application.cache.service.goods.impl;

import com.alibaba.fastjson.JSON;
import com.tiv.seckill.application.builder.SeckillGoodsBuilder;
import com.tiv.seckill.application.cache.model.SeckillBusinessCache;
import com.tiv.seckill.application.cache.service.goods.SeckillGoodsCacheService;
import com.tiv.seckill.domain.constants.Constants;
import com.tiv.seckill.domain.model.SeckillGoods;
import com.tiv.seckill.domain.repository.SeckillGoodsRepository;
import com.tiv.seckill.infra.cache.distributed.DistributedCacheService;
import com.tiv.seckill.infra.cache.local.LocalCacheService;
import com.tiv.seckill.infra.lock.DistributedLock;
import com.tiv.seckill.infra.lock.DistributedLockFactory;
import com.tiv.seckill.infra.util.string.StringUtil;
import com.tiv.seckill.infra.util.time.SystemClock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
public class SeckillGoodsCacheServiceImpl implements SeckillGoodsCacheService {

    @Autowired
    private LocalCacheService<Long, SeckillBusinessCache<SeckillGoods>> localCacheService;

    @Autowired
    private DistributedCacheService distributedCacheService;

    @Autowired
    private DistributedLockFactory distributedLockFactory;

    @Autowired
    private SeckillGoodsRepository seckillGoodsRepository;

    private static final String SECKILL_GOODS_UPDATE_CACHE_LOCK_KEY = "SECKILL_GOODS_UPDATE_CACHE_LOCK_KEY";

    private final Lock localCacheUpdateLock = new ReentrantLock();

    @Override
    public SeckillBusinessCache<SeckillGoods> getCachedGoods(Long goodsId, Long version) {
        SeckillBusinessCache<SeckillGoods> seckillGoodsCache = localCacheService.getIfPresent(goodsId);
        if (seckillGoodsCache == null) {
            return getDistributedCache(goodsId);
        }
        if (version == null || version.compareTo(seckillGoodsCache.getVersion()) <= 0) {
            log.info("seckillGoodsCache|命中本地缓存|{}", goodsId);
            return seckillGoodsCache;
        }
        return getDistributedCache(goodsId);
    }

    private SeckillBusinessCache<SeckillGoods> getDistributedCache(Long goodsId) {
        String cacheKey = buildCacheKey(goodsId);
        SeckillBusinessCache<SeckillGoods> seckillGoodsCache = SeckillGoodsBuilder.getSeckillBusinessCache(distributedCacheService.getObject(cacheKey), SeckillGoods.class);
        if (seckillGoodsCache == null) {
            // 分布式缓存不存在,尝试更新分布式缓存
            seckillGoodsCache = tryUpdateSeckillGoodsCacheByLock(goodsId);
        }
        if (seckillGoodsCache != null && !seckillGoodsCache.isRetryLater()) {
            // 获取分布式缓存后,尝试加锁更新本地缓存
            if (localCacheUpdateLock.tryLock()) {
                try {
                    localCacheService.put(goodsId, seckillGoodsCache);
                    log.info("SeckillGoodsCache|更新本地缓存成功|{}", goodsId);
                } finally {
                    localCacheUpdateLock.unlock();
                }
            }
        }
        return seckillGoodsCache;
    }

    @Override
    public SeckillBusinessCache<SeckillGoods> tryUpdateSeckillGoodsCacheByLock(Long goodsId) {
        DistributedLock distributedLock = distributedLockFactory.getDistributedLock(SECKILL_GOODS_UPDATE_CACHE_LOCK_KEY.concat(String.valueOf(goodsId)));
        try {
            // 尝试获取分布式锁
            if (!distributedLock.tryLock(2L, 5L, TimeUnit.SECONDS)) {
                // 获取锁失败,直接返回
                return new SeckillBusinessCache<SeckillGoods>().retryLater();
            }
            // 获取分布式锁成功,查询数据库
            SeckillGoods seckillGoods = seckillGoodsRepository.getSeckillGoodsById(goodsId);
            SeckillBusinessCache<SeckillGoods> seckillGoodsCache;
            if (seckillGoods == null) {
                seckillGoodsCache = new SeckillBusinessCache<SeckillGoods>().notExist();
            } else {
                seckillGoodsCache = new SeckillBusinessCache<SeckillGoods>().with(seckillGoods)
                        .withVersion(SystemClock.millisClock().now());
            }
            // 更新分布式缓存
            distributedCacheService.put(buildCacheKey(goodsId), JSON.toJSONString(seckillGoodsCache), 5 * 60);
            log.info("seckillGoodsCache|更新分布式缓存成功|{}", goodsId);
            return seckillGoodsCache;
        } catch (Exception e) {
            log.error("seckillGoodsCache|更新分布式缓存失败|{}", goodsId, e);
            return new SeckillBusinessCache<SeckillGoods>().retryLater();
        } finally {
            distributedLock.unLock();
        }
    }

    @Override
    public String buildCacheKey(Object key) {
        return StringUtil.append(Constants.SECKILL_GOODS_CACHE_KEY, key);
    }

}