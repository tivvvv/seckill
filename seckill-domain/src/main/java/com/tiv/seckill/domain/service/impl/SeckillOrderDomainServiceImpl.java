package com.tiv.seckill.domain.service.impl;

import com.tiv.seckill.domain.code.ErrorCodeEnum;
import com.tiv.seckill.domain.enums.SeckillOrderStatusEnum;
import com.tiv.seckill.domain.event.SeckillOrderEvent;
import com.tiv.seckill.domain.event.publisher.EventPublisher;
import com.tiv.seckill.domain.exception.BusinessException;
import com.tiv.seckill.domain.model.SeckillOrder;
import com.tiv.seckill.domain.repository.SeckillOrderRepository;
import com.tiv.seckill.domain.service.SeckillOrderDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SeckillOrderDomainServiceImpl implements SeckillOrderDomainService {

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private SeckillOrderRepository seckillOrderRepository;

    @Override
    public boolean saveSeckillOrder(SeckillOrder seckillOrder) {
        if (seckillOrder == null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "秒杀订单参数异常");
        }
        seckillOrder.setStatus(SeckillOrderStatusEnum.CREATED.getCode());
        boolean saveSuccess = seckillOrderRepository.saveSeckillOrder(seckillOrder);
        if (saveSuccess) {
            SeckillOrderEvent seckillOrderEvent = new SeckillOrderEvent(seckillOrder.getId(), seckillOrder.getStatus());
            eventPublisher.publish(seckillOrderEvent);
        }
        return saveSuccess;
    }

    @Override
    public List<SeckillOrder> getSeckillOrderByUserId(Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "userId 为 null");
        }
        return seckillOrderRepository.getSeckillOrderByUserId(userId);
    }

    @Override
    public List<SeckillOrder> getSeckillOrderByActivityId(Long activityId) {
        if (activityId == null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "activityId 为 null");
        }
        return seckillOrderRepository.getSeckillOrderByActivityId(activityId);
    }

}