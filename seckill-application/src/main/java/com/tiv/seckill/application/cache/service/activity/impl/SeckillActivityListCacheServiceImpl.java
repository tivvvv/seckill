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
    private static final String SECKILL_ACTIVITIES_UPDATE_CACHE_LOCK_KEY = "SECKILL_ACTIVITIES_UPDATE_CACHE_LOCK_KEY_";

    /**
     * 本地缓存更新锁
     */
    private final Lock localCacheUpdateLock = new ReentrantLock();

    @Override
    public SeckillBusinessCache<List<SeckillActivity>> getCachedActivities(Integer status, Long version) {
        // 获取本地缓存
        SeckillBusinessCache<List<SeckillActivity>> seckillActivitiesCache = localCacheService.getIfPresent(status.longValue());
        if (seckillActivitiesCache == null) {
            // 本地缓存不存在,获取分布式缓存
            return getDistributedCache(status);
        }
        if (version == null || version.compareTo(seckillActivitiesCache.getVersion()) <= 0) {
            log.info("seckillActivitiesCache|命中本地缓存|{}", status);
            return seckillActivitiesCache;
        } else {
            // 本地缓存过期了,获取分布式缓存
            return getDistributedCache(status);
        }
    }

    private SeckillBusinessCache<List<SeckillActivity>> getDistributedCache(Integer status) {
        String cacheKey = buildCacheKey(status);
        SeckillBusinessCache<List<SeckillActivity>> seckillActivitiesCache = SeckillActivityBuilder.getSeckillBusinessCacheList(distributedCacheService.getObject(cacheKey), SeckillActivity.class);
        if (seckillActivitiesCache == null) {
            // 分布式缓存不存在,尝试更新分布式缓存
            seckillActivitiesCache = tryUpdateSeckillActivitiesCacheByLock(status);
        }
        if (seckillActivitiesCache != null && !seckillActivitiesCache.isRetryLater()) {
            // 获取分布式缓存后,尝试加锁更新本地缓存
            if (localCacheUpdateLock.tryLock()) {
                try {
                    localCacheService.put(status.longValue(), seckillActivitiesCache);
                    log.info("seckillActivitiesCache|更新本地缓存成功|{}", status);
                } finally {
                    localCacheUpdateLock.unlock();
                }
            }
        }
        return seckillActivitiesCache;
    }

    @Override
    public SeckillBusinessCache<List<SeckillActivity>> tryUpdateSeckillActivitiesCacheByLock(Integer status) {
        DistributedLock distributedLock = distributedLockFactory.getDistributedLock(SECKILL_ACTIVITIES_UPDATE_CACHE_LOCK_KEY.concat(String.valueOf(status)));
        try {
            // 尝试获取分布式锁
            if (!distributedLock.tryLock(1L, 5L, TimeUnit.SECONDS)) {
                // 获取锁失败,直接返回
                return new SeckillBusinessCache<List<SeckillActivity>>().retryLater();
            }
            // 获取分布式锁成功,查询数据库
            List<SeckillActivity> seckillActivityList = seckillActivityRepository.getSeckillActivityList(status);
            SeckillBusinessCache<List<SeckillActivity>> seckillActivitiesCache;
            if (CollectionUtils.isEmpty(seckillActivityList)) {
                seckillActivitiesCache = new SeckillBusinessCache<List<SeckillActivity>>().notExist();
            } else {
                seckillActivitiesCache = new SeckillBusinessCache<List<SeckillActivity>>().with(seckillActivityList)
                        .withVersion(SystemClock.millisClock().now());
            }
            // 更新分布式缓存
            distributedCacheService.put(buildCacheKey(status), JSON.toJSONString(seckillActivitiesCache), 5 * 60);
            log.info("seckillActivitiesCache|更新分布式缓存成功|{}", status);
            return seckillActivitiesCache;
        } catch (Exception e) {
            log.error("seckillActivitiesCache|更新分布式缓存失败|{}", status, e);
            return new SeckillBusinessCache<List<SeckillActivity>>().retryLater();
        } finally {
            distributedLock.unLock();
        }
    }

    @Override
    public String buildCacheKey(Object key) {
        return StringUtil.append(Constants.SECKILL_ACTIVITIES_CACHE_KEY, key);
    }

}