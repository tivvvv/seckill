package com.tiv.seckill.application.service.impl;

import com.tiv.seckill.application.builder.SeckillActivityBuilder;
import com.tiv.seckill.application.cache.model.SeckillBusinessCache;
import com.tiv.seckill.application.cache.service.activity.SeckillActivityCacheService;
import com.tiv.seckill.application.cache.service.activity.SeckillActivityListCacheService;
import com.tiv.seckill.application.service.SeckillActivityService;
import com.tiv.seckill.domain.code.ErrorCodeEnum;
import com.tiv.seckill.domain.dto.SeckillActivityDTO;
import com.tiv.seckill.domain.enums.SeckillActivityStatusEnum;
import com.tiv.seckill.domain.exception.BusinessException;
import com.tiv.seckill.domain.model.SeckillActivity;
import com.tiv.seckill.domain.repository.SeckillActivityRepository;
import com.tiv.seckill.infra.util.bean.BeanUtil;
import com.tiv.seckill.infra.util.id.SnowFlakeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SeckillActivityServiceImpl implements SeckillActivityService {

    @Autowired
    private SeckillActivityRepository seckillActivityRepository;

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

        seckillActivityRepository.saveSeckillActivity(seckillActivity);
    }

    @Override
    public int updateStatus(Long id, Integer status) {
        return seckillActivityRepository.updateStatus(id, status);
    }

    @Override
    public List<SeckillActivity> getSeckillActivityDTOList(Integer status) {
        return seckillActivityRepository.getSeckillActivityList(status);
    }

    @Override
    public List<SeckillActivity> getSeckillActivityListByNow(Date currentTime, Integer status) {
        return seckillActivityRepository.getSeckillActivityListByNow(currentTime, status);
    }

    @Override
    public SeckillActivity getSeckillActivityById(Long id) {
        return seckillActivityRepository.getSeckillActivityById(id);
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
                    SeckillActivityDTO seckillActivityDTO = new SeckillActivityDTO();
                    BeanUtil.copyProperties(seckillActivity, seckillActivityDTO);
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