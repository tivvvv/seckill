package com.tiv.seckill.order.domain.service.impl;

import com.tiv.seckill.common.event.publisher.EventPublisher;
import com.tiv.seckill.common.exception.BusinessException;
import com.tiv.seckill.common.exception.ErrorCodeEnum;
import com.tiv.seckill.common.model.enums.SeckillOrderStatusEnum;
import com.tiv.seckill.order.domain.event.SeckillOrderEvent;
import com.tiv.seckill.order.domain.model.SeckillOrder;
import com.tiv.seckill.order.domain.repository.SeckillOrderRepository;
import com.tiv.seckill.order.domain.service.SeckillOrderDomainService;
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

    @Override
    public void deleteSeckillOrder(Long orderId) {
        if (orderId == null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "orderId 为 null");
        }
        if (seckillOrderRepository.deleteSeckillOrder(orderId)) {
            log.info("deleteSeckillOrder|删除订单成功|{}", orderId);
            SeckillOrderEvent seckillOrderEvent = new SeckillOrderEvent(orderId, SeckillOrderStatusEnum.DELETED.getCode());
            eventPublisher.publish(seckillOrderEvent);
        }
    }

}