package com.tiv.seckill.application.cache.service.activity.impl;

import com.alibaba.fastjson.JSON;
import com.tiv.seckill.application.builder.SeckillActivityBuilder;
import com.tiv.seckill.application.cache.model.SeckillBusinessCache;
import com.tiv.seckill.application.cache.service.activity.SeckillActivityListCacheService;
import com.tiv.seckill.domain.constants.Constants;
import com.tiv.seckill.domain.model.SeckillActivity;
import com.tiv.seckill.domain.repository.SeckillActivityRepository;
import com.tiv.seckill.infra.cache.distributed.DistributedCacheService;
import com.tiv.seckill.infra.cache.local.LocalCacheService;
import com.tiv.seckill.infra.lock.DistributedLock;
import com.tiv.seckill.infra.lock.DistributedLockFactory;
import com.tiv.seckill.infra.util.string.StringUtil;
import com.tiv.seckill.infra.util.time.SystemClock;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
public class SeckillActivityListCacheServiceImpl implements SeckillActivityListCacheService {

    @Autowired
    private LocalCacheService<Long, SeckillBusinessCache<List<SeckillActivity>>> localCacheService;

    @Autowired
    private DistributedCacheService distributedCacheService;

    @Autowired
    private DistributedLockFactory distributedLockFactory;

    @Autowired
    private SeckillActivityRepository seckillActivityRepository;

    /**
     * 秒杀活动列表更新缓存分布式锁key
     */
    private static final String SECKILL_ACTIVITY_LIST_UPDATE_CACHE_LOCK_KEY = "SECKILL_ACTIVITY_LIST_UPDATE_CACHE_LOCK_KEY";

    /**
     * 本地缓存更新锁
     */
    private final Lock localCacheUpdateLock = new ReentrantLock();

    @Override
    public SeckillBusinessCache<List<SeckillActivity>> getCachedActivityList(Integer status, Long version) {
        // 获取本地缓存
        SeckillBusinessCache<List<SeckillActivity>> seckillActivityListCache = localCacheService.getIfPresent(status.longValue());
        if (seckillActivityListCache == null) {
            // 本地缓存不存在,获取分布式缓存
            return getDistributedCache(status);
        }
        if (version == null || version.compareTo(seckillActivityListCache.getVersion()) <= 0) {
            log.info("seckillActivityListCache|命中本地缓存|{}", status);
            return seckillActivityListCache;
        } else {
            // 本地缓存过期了,获取分布式缓存
            return getDistributedCache(status);
        }
    }

    private SeckillBusinessCache<List<SeckillActivity>> getDistributedCache(Integer status) {
        String cacheKey = buildCacheKey(status);
        SeckillBusinessCache<List<SeckillActivity>> seckillActivityListCache = SeckillActivityBuilder.getSeckillBusinessCacheList(distributedCacheService.getObject(cacheKey), SeckillActivity.class);
        if (seckillActivityListCache == null) {
            // 分布式缓存不存在,尝试更新分布式缓存
            seckillActivityListCache = tryUpdateSeckillActivityListCacheByLock(status);
        }
        if (seckillActivityListCache != null && !seckillActivityListCache.isRetryLater()) {
            // 获取分布式缓存后,尝试加锁更新本地缓存
            if (localCacheUpdateLock.tryLock()) {
                try {
                    localCacheService.put(status.longValue(), seckillActivityListCache);
                    log.info("seckillActivityListCache|更新本地缓存成功|{}", status);
                } finally {
                    localCacheUpdateLock.unlock();
                }
            }
        }
        return seckillActivityListCache;
    }

    @Override
    public SeckillBusinessCache<List<SeckillActivity>> tryUpdateSeckillActivityListCacheByLock(Integer status) {
        DistributedLock distributedLock = distributedLockFactory.getDistributedLock(SECKILL_ACTIVITY_LIST_UPDATE_CACHE_LOCK_KEY.concat(String.valueOf(status)));
        try {
            // 尝试获取分布式锁
            if (!distributedLock.tryLock(1L, 5L, TimeUnit.SECONDS)) {
                // 获取锁失败,直接返回
                return new SeckillBusinessCache<List<SeckillActivity>>().retryLater();
            }
            // 获取分布式锁成功,查询数据库
            List<SeckillActivity> seckillActivityList = seckillActivityRepository.getSeckillActivityList(status);
            SeckillBusinessCache<List<SeckillActivity>> seckillActivityListCache;
            if (CollectionUtils.isEmpty(seckillActivityList)) {
                seckillActivityListCache = new SeckillBusinessCache<List<SeckillActivity>>().notExist();
            } else {
                seckillActivityListCache = new SeckillBusinessCache<List<SeckillActivity>>().with(seckillActivityList)
                        .withVersion(SystemClock.millisClock().now());
            }
            // 更新分布式缓存
            distributedCacheService.put(buildCacheKey(status), JSON.toJSONString(seckillActivityListCache), 5 * 60);
            log.info("seckillActivityListCache|更新分布式缓存成功|{}", status);
            return seckillActivityListCache;
        } catch (Exception e) {
            log.error("seckillActivityListCache|更新分布式缓存失败|{}", status, e);
            return new SeckillBusinessCache<List<SeckillActivity>>().retryLater();
        } finally {
            distributedLock.unLock();
        }
    }

    @Override
    public String buildCacheKey(Object key) {
        return StringUtil.append(Constants.SECKILL_ACTIVITY_LIST_CACHE_KEY, key);
    }

}