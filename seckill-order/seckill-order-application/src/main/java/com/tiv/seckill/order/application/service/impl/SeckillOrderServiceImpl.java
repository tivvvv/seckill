package com.tiv.seckill.order.application.service.impl;

import com.tiv.seckill.common.exception.BusinessException;
import com.tiv.seckill.common.exception.ErrorCodeEnum;
import com.tiv.seckill.order.application.command.SeckillOrderCommand;
import com.tiv.seckill.order.application.place.SeckillPlaceOrderService;
import com.tiv.seckill.order.application.service.SeckillOrderService;
import com.tiv.seckill.order.domain.model.SeckillOrder;
import com.tiv.seckill.order.domain.service.SeckillOrderDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private SeckillOrderDomainService seckillOrderDomainService;

    @Autowired
    private SeckillPlaceOrderService seckillPlaceOrderService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveSeckillOrder(Long userId, SeckillOrderCommand seckillOrderCommand) {
        if (seckillOrderCommand == null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR);
        }
        return seckillPlaceOrderService.placeOrder(userId, seckillOrderCommand);
    }

    @Override
    public List<SeckillOrder> getSeckillOrderByUserId(Long userId) {
        return seckillOrderDomainService.getSeckillOrderByUserId(userId);
    }

    @Override
    public List<SeckillOrder> getSeckillOrderByActivityId(Long activityId) {
        return seckillOrderDomainService.getSeckillOrderByActivityId(activityId);
    }

}