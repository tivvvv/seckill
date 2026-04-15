package com.tiv.seckill.domain.service.impl;

import com.tiv.seckill.domain.code.ErrorCodeEnum;
import com.tiv.seckill.domain.enums.SeckillActivityStatusEnum;
import com.tiv.seckill.domain.event.SeckillActivityEvent;
import com.tiv.seckill.domain.event.publisher.EventPublisher;
import com.tiv.seckill.domain.exception.BusinessException;
import com.tiv.seckill.domain.model.SeckillActivity;
import com.tiv.seckill.domain.repository.SeckillActivityRepository;
import com.tiv.seckill.domain.service.SeckillActivityDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class SeckillActivityDomainServiceImpl implements SeckillActivityDomainService {

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private SeckillActivityRepository seckillActivityRepository;

    @Override
    public void saveSeckillActivity(SeckillActivity seckillActivity) {
        if (seckillActivity == null || !seckillActivity.validateParams()) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "秒杀活动参数异常");
        }
        seckillActivity.setStatus(SeckillActivityStatusEnum.PUBLISHED.getCode());
        seckillActivityRepository.saveSeckillActivity(seckillActivity);

        SeckillActivityEvent seckillActivityEvent = new SeckillActivityEvent(seckillActivity.getId(), seckillActivity.getStatus());
        eventPublisher.publish(seckillActivityEvent);
        log.info("activityPublish|发布秒杀活动事件发布成功|{}", seckillActivityEvent);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        if (id == null || status == null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "秒杀活动参数异常");
        }
        seckillActivityRepository.updateStatus(id, status);
        SeckillActivityEvent seckillActivityEvent = new SeckillActivityEvent(id, status);
        eventPublisher.publish(seckillActivityEvent);
        log.info("activityPublish|更新秒杀活动状态事件发布成功|{},{}", id, status);
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

}