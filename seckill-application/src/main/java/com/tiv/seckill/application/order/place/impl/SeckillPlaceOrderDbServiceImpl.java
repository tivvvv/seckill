package com.tiv.seckill.application.order.place.impl;

import com.tiv.seckill.application.command.SeckillOrderCommand;
import com.tiv.seckill.application.order.place.SeckillPlaceOrderService;
import com.tiv.seckill.application.service.SeckillGoodsService;
import com.tiv.seckill.domain.code.ErrorCodeEnum;
import com.tiv.seckill.domain.dto.SeckillGoodsDTO;
import com.tiv.seckill.domain.exception.BusinessException;
import com.tiv.seckill.domain.model.SeckillOrder;
import com.tiv.seckill.domain.service.SeckillOrderDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(name = "order.place.type", havingValue = "db")
public class SeckillPlaceOrderDbServiceImpl implements SeckillPlaceOrderService {

    @Autowired
    private SeckillGoodsService seckillGoodsService;

    @Autowired
    private SeckillOrderDomainService seckillOrderDomainService;

    @Override
    public Long placeOrder(Long userId, SeckillOrderCommand seckillOrderCommand) {
        SeckillGoodsDTO seckillGoodsDTO = seckillGoodsService.getSeckillGoodsDTO(seckillOrderCommand.getGoodsId(), seckillOrderCommand.getVersion());
        // 校验商品状态
        this.checkSeckillGoods(seckillOrderCommand, seckillGoodsDTO);
        // 扣减库存
        if (!seckillGoodsService.decreaseAvailableDbStock(seckillOrderCommand.getGoodsId(), seckillOrderCommand.getQuantity())) {
            throw new BusinessException(ErrorCodeEnum.OPERATION_ERROR, "库存不足");
        }
        // 构建订单
        SeckillOrder seckillOrder = this.buildSeckillOrder(userId, seckillOrderCommand, seckillGoodsDTO);
        // 保存订单
        seckillOrderDomainService.saveSeckillOrder(seckillOrder);
        return seckillOrder.getId();
    }

}