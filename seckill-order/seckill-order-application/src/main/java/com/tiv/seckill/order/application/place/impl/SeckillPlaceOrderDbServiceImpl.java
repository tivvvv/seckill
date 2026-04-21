package com.tiv.seckill.order.application.place.impl;

import com.tiv.seckill.common.exception.BusinessException;
import com.tiv.seckill.common.exception.ErrorCodeEnum;
import com.tiv.seckill.common.model.dto.SeckillGoodsDTO;
import com.tiv.seckill.dubbo.interfaces.goods.SeckillGoodsDubboService;
import com.tiv.seckill.order.application.command.SeckillOrderCommand;
import com.tiv.seckill.order.application.place.SeckillPlaceOrderService;
import com.tiv.seckill.order.domain.model.SeckillOrder;
import com.tiv.seckill.order.domain.service.SeckillOrderDomainService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(name = "order.place.type", havingValue = "db")
public class SeckillPlaceOrderDbServiceImpl implements SeckillPlaceOrderService {

    @DubboReference(version = "1.0.0")
    private SeckillGoodsDubboService seckillGoodsDubboService;

    @Autowired
    private SeckillOrderDomainService seckillOrderDomainService;

    @Override
    public Long placeOrder(Long userId, SeckillOrderCommand seckillOrderCommand) {
        SeckillGoodsDTO seckillGoodsDTO = seckillGoodsDubboService.getSeckillGoodsDTO(seckillOrderCommand.getGoodsId(), seckillOrderCommand.getVersion());
        // 校验商品状态
        this.checkSeckillGoods(seckillOrderCommand, seckillGoodsDTO);
        // 扣减库存
        if (!seckillGoodsDubboService.decreaseAvailableDbStock(seckillOrderCommand.getGoodsId(), seckillOrderCommand.getQuantity())) {
            throw new BusinessException(ErrorCodeEnum.FORBIDDEN_ERROR, "库存不足");
        }
        // 构建订单
        SeckillOrder seckillOrder = this.buildSeckillOrder(userId, seckillOrderCommand, seckillGoodsDTO);
        // 保存订单
        seckillOrderDomainService.saveSeckillOrder(seckillOrder);
        return seckillOrder.getId();
    }

}