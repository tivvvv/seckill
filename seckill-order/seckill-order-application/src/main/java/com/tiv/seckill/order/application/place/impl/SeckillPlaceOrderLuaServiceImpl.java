package com.tiv.seckill.order.application.place.impl;

import com.tiv.seckill.common.cache.distributed.DistributedCacheService;
import com.tiv.seckill.common.constants.Constants;
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
@ConditionalOnProperty(name = "order.place.type", havingValue = "lua")
public class SeckillPlaceOrderLuaServiceImpl implements SeckillPlaceOrderService {

    @DubboReference(version = "1.0.0")
    private SeckillGoodsDubboService seckillGoodsDubboService;

    @Autowired
    private SeckillOrderDomainService seckillOrderDomainService;

    @Autowired
    private DistributedCacheService distributedCacheService;

    @Override
    public Long placeOrder(Long userId, SeckillOrderCommand seckillOrderCommand) {
        // 获取商品
        SeckillGoodsDTO seckillGoodsDTO = seckillGoodsDubboService.getSeckillGoodsDTO(seckillOrderCommand.getGoodsId(), seckillOrderCommand.getVersion());
        // 校验商品状态
        this.checkSeckillGoods(seckillOrderCommand, seckillGoodsDTO);

        String cacheKey = Constants.getKey(Constants.SECKILL_GOODS_STOCK_CACHE_KEY, String.valueOf(seckillOrderCommand.getGoodsId()));
        boolean isDecrementCachedStock = false;

        try {
            Long result = distributedCacheService.decrementByLua(cacheKey, seckillOrderCommand.getQuantity());
            distributedCacheService.checkLuaResult(result);
            isDecrementCachedStock = true;
            // 构建订单
            SeckillOrder seckillOrder = this.buildSeckillOrder(userId, seckillOrderCommand, seckillGoodsDTO);
            // 保存订单
            seckillOrderDomainService.saveSeckillOrder(seckillOrder);
            // 扣减数据库库存
            seckillGoodsDubboService.decreaseAvailableDbStock(seckillOrderCommand.getGoodsId(), seckillOrderCommand.getQuantity());
            return seckillOrder.getId();
        } catch (Exception e) {
            if (isDecrementCachedStock) {
                distributedCacheService.incrementByLua(cacheKey, seckillOrderCommand.getQuantity());
            }

            throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, e.getMessage());
        }
    }

}