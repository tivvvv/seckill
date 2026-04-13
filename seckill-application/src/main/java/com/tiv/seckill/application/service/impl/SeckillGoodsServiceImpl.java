package com.tiv.seckill.application.service.impl;

import com.tiv.seckill.application.builder.SeckillGoodsBuilder;
import com.tiv.seckill.application.cache.model.SeckillBusinessCache;
import com.tiv.seckill.application.cache.service.goods.SeckillGoodsCacheService;
import com.tiv.seckill.application.cache.service.goods.SeckillGoodsListCacheService;
import com.tiv.seckill.application.service.SeckillGoodsService;
import com.tiv.seckill.domain.code.ErrorCodeEnum;
import com.tiv.seckill.domain.dto.SeckillGoodsDTO;
import com.tiv.seckill.domain.enums.SeckillActivityStatusEnum;
import com.tiv.seckill.domain.exception.BusinessException;
import com.tiv.seckill.domain.model.SeckillActivity;
import com.tiv.seckill.domain.model.SeckillGoods;
import com.tiv.seckill.domain.repository.SeckillActivityRepository;
import com.tiv.seckill.domain.repository.SeckillGoodsRepository;
import com.tiv.seckill.infra.util.bean.BeanUtil;
import com.tiv.seckill.infra.util.id.SnowFlakeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

    @Autowired
    private SeckillGoodsRepository seckillGoodsRepository;

    @Autowired
    private SeckillActivityRepository seckillActivityRepository;

    @Autowired
    private SeckillGoodsListCacheService seckillGoodsListCacheService;

    @Autowired
    private SeckillGoodsCacheService seckillGoodsCacheService;

    @Override
    public int saveSeckillGoods(SeckillGoodsDTO seckillGoodsDTO) {
        if (seckillGoodsDTO == null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "seckillGoodsDTO 为 null");
        }

        SeckillActivity seckillActivity = seckillActivityRepository.getSeckillActivityById(seckillGoodsDTO.getActivityId());
        if (seckillActivity == null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "秒杀活动不存在");
        }

        SeckillGoods seckillGoods = new SeckillGoods();
        BeanUtil.copyProperties(seckillGoodsDTO, seckillGoods);
        seckillGoods.setId(SnowFlakeFactory.getSnowFlakeFromCache().nextId());
        seckillGoods.setStatus(SeckillActivityStatusEnum.PUBLISHED.getCode());
        seckillGoods.setAvailableStock(seckillGoodsDTO.getInitialStock());
        seckillGoods.setStartTime(seckillActivity.getStartTime());
        seckillGoods.setEndTime(seckillActivity.getEndTime());
        return seckillGoodsRepository.saveSeckillGoods(seckillGoods);
    }

    @Override
    public int updateStatus(Long id, Integer status) {
        return seckillGoodsRepository.updateStatus(id, status);
    }

    @Override
    public int decreaseAvailableStock(Long id, Integer count) {
        return seckillGoodsRepository.decreaseAvailableStock(id, count);
    }

    @Override
    public SeckillGoods getSeckillGoodsById(Long id) {
        return seckillGoodsRepository.getSeckillGoodsById(id);
    }

    @Override
    public List<SeckillGoods> getSeckillGoodsByActivityId(Long activityId) {
        return seckillGoodsRepository.getSeckillGoodsListByActivityId(activityId);
    }

    @Override
    public Integer getAvailableStockById(Long id) {
        return seckillGoodsRepository.getAvailableStockById(id);
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