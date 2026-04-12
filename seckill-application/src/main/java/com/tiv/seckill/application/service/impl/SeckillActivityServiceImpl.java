package com.tiv.seckill.application.service.impl;

import com.tiv.seckill.application.cache.model.SeckillBusinessCache;
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
    public List<SeckillActivity> getSeckillActivityList(Integer status) {
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
    public List<SeckillActivityDTO> getSeckillActivityList(Integer status, Long version) {
        SeckillBusinessCache<List<SeckillActivity>> seckillActivitiesCache = seckillActivityListCacheService.getCachedActivities(status, version);
        if (!seckillActivitiesCache.isExist()) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "活动不存在");
        }
        if (seckillActivitiesCache.isRetryLater()) {
            throw new BusinessException(ErrorCodeEnum.RETRY_LATER);
        }
        return seckillActivitiesCache.getData().stream()
                .map(seckillActivity -> {
                    SeckillActivityDTO seckillActivityDTO = new SeckillActivityDTO();
                    BeanUtil.copyProperties(seckillActivity, seckillActivityDTO);
                    return seckillActivityDTO;
                }).toList();
    }

}