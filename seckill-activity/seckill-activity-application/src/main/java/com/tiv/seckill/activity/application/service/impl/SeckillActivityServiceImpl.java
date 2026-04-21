package com.tiv.seckill.activity.application.service.impl;

import com.tiv.seckill.activity.application.builder.SeckillActivityBuilder;
import com.tiv.seckill.activity.application.cache.service.SeckillActivityCacheService;
import com.tiv.seckill.activity.application.cache.service.SeckillActivityListCacheService;
import com.tiv.seckill.activity.application.service.SeckillActivityService;
import com.tiv.seckill.activity.domain.model.SeckillActivity;
import com.tiv.seckill.activity.domain.service.SeckillActivityDomainService;
import com.tiv.seckill.common.cache.model.SeckillBusinessCache;
import com.tiv.seckill.common.exception.BusinessException;
import com.tiv.seckill.common.exception.ErrorCodeEnum;
import com.tiv.seckill.common.model.dto.SeckillActivityDTO;
import com.tiv.seckill.common.model.enums.SeckillActivityStatusEnum;
import com.tiv.seckill.common.util.bean.BeanUtil;
import com.tiv.seckill.common.util.id.SnowFlakeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SeckillActivityServiceImpl implements SeckillActivityService {

    @Autowired
    private SeckillActivityDomainService seckillActivityDomainService;

    @Autowired
    private SeckillActivityListCacheService seckillActivityListCacheService;

    @Autowired
    private SeckillActivityCacheService seckillActivityCacheService;

    @Override
    public void saveSeckillActivity(SeckillActivityDTO seckillActivityDTO) {
        if (seckillActivityDTO == null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "seckillActivityDTO 为 null");
        }
        SeckillActivity seckillActivity = new SeckillActivity();
        BeanUtil.copyProperties(seckillActivityDTO, seckillActivity);
        seckillActivity.setId(SnowFlakeFactory.getSnowFlakeFromCache().nextId());
        seckillActivity.setStatus(SeckillActivityStatusEnum.PUBLISHED.getCode());

        seckillActivityDomainService.saveSeckillActivity(seckillActivity);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        seckillActivityDomainService.updateStatus(id, status);
    }

    @Override
    public List<SeckillActivity> getSeckillActivityDTOList(Integer status) {
        return seckillActivityDomainService.getSeckillActivityList(status);
    }

    @Override
    public List<SeckillActivity> getSeckillActivityListByNow(Date currentTime, Integer status) {
        return seckillActivityDomainService.getSeckillActivityListByNow(currentTime, status);
    }

    @Override
    public SeckillActivity getSeckillActivityById(Long id) {
        return seckillActivityDomainService.getSeckillActivityById(id);
    }

    @Override
    public List<SeckillActivityDTO> getSeckillActivityDTOList(Integer status, Long version) {
        SeckillBusinessCache<List<SeckillActivity>> seckillActivityListCache = seckillActivityListCacheService.getCachedActivityList(status, version);
        if (!seckillActivityListCache.isExist()) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "活动不存在");
        }
        if (seckillActivityListCache.isRetryLater()) {
            throw new BusinessException(ErrorCodeEnum.RETRY_LATER);
        }
        return seckillActivityListCache.getData().stream()
                .map(seckillActivity -> {
                    SeckillActivityDTO seckillActivityDTO = SeckillActivityBuilder.toSeckillActivityDTO(seckillActivity);
                    seckillActivityDTO.setVersion(seckillActivityListCache.getVersion());
                    return seckillActivityDTO;
                }).toList();
    }

    @Override
    public SeckillActivityDTO getSeckillActivityDTO(Long id, Long version) {
        if (id == null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR);
        }
        SeckillBusinessCache<SeckillActivity> seckillActivityCache = seckillActivityCacheService.getCachedActivity(id, version);
        if (!seckillActivityCache.isExist()) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "活动不存在");
        }
        if (seckillActivityCache.isRetryLater()) {
            throw new BusinessException(ErrorCodeEnum.RETRY_LATER);
        }
        SeckillActivityDTO seckillActivityDTO = SeckillActivityBuilder.toSeckillActivityDTO(seckillActivityCache.getData());
        seckillActivityDTO.setVersion(seckillActivityCache.getVersion());
        return seckillActivityDTO;
    }

}