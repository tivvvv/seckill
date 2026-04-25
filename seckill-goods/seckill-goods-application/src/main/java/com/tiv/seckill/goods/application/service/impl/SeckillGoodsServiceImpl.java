package com.tiv.seckill.goods.application.service.impl;

import com.tiv.seckill.common.cache.distributed.DistributedCacheService;
import com.tiv.seckill.common.cache.model.SeckillBusinessCache;
import com.tiv.seckill.common.constants.Constants;
import com.tiv.seckill.common.exception.BusinessException;
import com.tiv.seckill.common.exception.ErrorCodeEnum;
import com.tiv.seckill.common.model.dto.SeckillActivityDTO;
import com.tiv.seckill.common.model.dto.SeckillGoodsDTO;
import com.tiv.seckill.common.model.enums.SeckillActivityStatusEnum;
import com.tiv.seckill.common.util.id.SnowFlakeFactory;
import com.tiv.seckill.dubbo.interfaces.activity.SeckillActivityDubboService;
import com.tiv.seckill.goods.application.builder.SeckillGoodsBuilder;
import com.tiv.seckill.goods.application.cache.service.SeckillGoodsCacheService;
import com.tiv.seckill.goods.application.cache.service.SeckillGoodsListCacheService;
import com.tiv.seckill.goods.application.command.SeckillGoodsCommand;
import com.tiv.seckill.goods.application.service.SeckillGoodsService;
import com.tiv.seckill.goods.domain.model.SeckillGoods;
import com.tiv.seckill.goods.domain.service.SeckillGoodsDomainService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

    @Autowired
    private SeckillGoodsDomainService seckillGoodsDomainService;

    @DubboReference(version = "1.0.0")
    private SeckillActivityDubboService seckillActivityDubboService;

    @Autowired
    private SeckillGoodsListCacheService seckillGoodsListCacheService;

    @Autowired
    private SeckillGoodsCacheService seckillGoodsCacheService;

    @Autowired
    private DistributedCacheService distributedCacheService;

    @Override
    public void saveSeckillGoods(SeckillGoodsCommand seckillGoodsCommand) {
        if (seckillGoodsCommand == null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "seckillGoodsCommand 为 null");
        }

        SeckillActivityDTO seckillActivityDTO = seckillActivityDubboService.getSeckillActivityDTO(seckillGoodsCommand.getActivityId(), seckillGoodsCommand.getActivityVersion());
        if (seckillActivityDTO == null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "秒杀活动不存在");
        }

        SeckillGoods seckillGoods = SeckillGoodsBuilder.toSeckillGoods(seckillGoodsCommand);
        seckillGoods.setId(SnowFlakeFactory.getSnowFlakeFromCache().nextId())
                .setStatus(SeckillActivityStatusEnum.PUBLISHED.getCode())
                .setAvailableStock(seckillGoodsCommand.getInitialStock())
                .setStartTime(seckillActivityDTO.getStartTime())
                .setEndTime(seckillActivityDTO.getEndTime());
        String cacheKey = Constants.getKey(Constants.SECKILL_GOODS_STOCK_CACHE_KEY, String.valueOf(seckillGoods.getId()));
        try {
            distributedCacheService.initByLua(cacheKey, seckillGoods.getAvailableStock());
            seckillGoodsDomainService.saveSeckillGoods(seckillGoods);
        } catch (Exception e) {
            if (distributedCacheService.hasKey(cacheKey)) {
                distributedCacheService.delete(cacheKey);
            }
            throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, e.getMessage());
        }
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        seckillGoodsDomainService.updateStatus(id, status);
    }

    @Override
    public boolean decreaseAvailableStock(Long id, Integer count) {
        return seckillGoodsDomainService.decreaseAvailableStock(id, count);
    }

    @Override
    public boolean decreaseAvailableDbStock(Long id, Integer count) {
        return seckillGoodsDomainService.decreaseAvailableDbStock(id, count);
    }

    @Override
    public boolean increaseAvailableStock(Long id, Integer count) {
        return seckillGoodsDomainService.increaseAvailableStock(id, count);
    }

    @Override
    public SeckillGoods getSeckillGoodsById(Long id) {
        return seckillGoodsDomainService.getSeckillGoodsById(id);
    }

    @Override
    public List<SeckillGoods> getSeckillGoodsByActivityId(Long activityId) {
        return seckillGoodsDomainService.getSeckillGoodsListByActivityId(activityId);
    }

    @Override
    public Integer getAvailableStockById(Long id) {
        return seckillGoodsDomainService.getAvailableStockById(id);
    }

    @Override
    public List<SeckillGoodsDTO> getSeckillGoodsDTOList(Long activityId, Long version) {
        if (activityId == null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "activityId 为 null");
        }
        SeckillBusinessCache<List<SeckillGoods>> seckillGoodsListCache = seckillGoodsListCacheService.getCachedGoodsList(activityId, version);
        if (!seckillGoodsListCache.isExist()) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "商品不存在");
        }
        if (seckillGoodsListCache.isRetryLater()) {
            throw new BusinessException(ErrorCodeEnum.RETRY_LATER);
        }
        return seckillGoodsListCache.getData().stream()
                .map(seckillGoods -> {
                    SeckillGoodsDTO seckillGoodsDTO = SeckillGoodsBuilder.toSeckillGoodsDTO(seckillGoods);
                    seckillGoodsDTO.setVersion(seckillGoodsListCache.getVersion());
                    return seckillGoodsDTO;
                }).toList();
    }

    @Override
    public SeckillGoodsDTO getSeckillGoodsDTO(Long id, Long version) {
        if (id == null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR);
        }
        SeckillBusinessCache<SeckillGoods> seckillGoodsCache = seckillGoodsCacheService.getCachedGoods(id, version);
        if (!seckillGoodsCache.isExist()) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "商品不存在");
        }
        if (seckillGoodsCache.isRetryLater()) {
            throw new BusinessException(ErrorCodeEnum.RETRY_LATER);
        }
        SeckillGoodsDTO seckillGoodsDTO = SeckillGoodsBuilder.toSeckillGoodsDTO(seckillGoodsCache.getData());
        seckillGoodsDTO.setVersion(seckillGoodsCache.getVersion());
        return seckillGoodsDTO;
    }

}