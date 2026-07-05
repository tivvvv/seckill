package com.tiv.seckill.order.application.place.impl;

import com.tiv.seckill.common.constants.Constants;
import com.tiv.seckill.common.exception.BusinessException;
import com.tiv.seckill.common.exception.ErrorCodeEnum;
import com.tiv.seckill.common.model.dto.SeckillGoodsDTO;
import com.tiv.seckill.order.application.command.SeckillOrderCommand;
import com.tiv.seckill.order.application.place.SeckillPlaceOrderService;
import com.tiv.seckill.order.domain.model.SeckillOrder;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hmily.annotation.HmilyTCC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@ConditionalOnProperty(name = "order.place.type", havingValue = "lua")
public class SeckillPlaceOrderLuaServiceImpl extends SeckillPlaceOrderBaseServiceImpl implements SeckillPlaceOrderService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    @HmilyTCC(confirmMethod = "confirmMethod", cancelMethod = "cancelMethod")
    public Long placeOrder(Long userId, SeckillOrderCommand seckillOrderCommand, Long txId) {
        String tryKey = Constants.getKey(Constants.ORDER_TRY_KEY_PREFIX, Constants.ORDER);
        // 幂等处理
        if (distributedCacheService.inSet(tryKey, txId)) {
            log.warn("placeOrder|基于Lua脚本实现库存防超卖-提交订单ry方法已执行过|{}", txId);
            return txId;
        }
        // 悬挂处理
        if (distributedCacheService.inSet(Constants.getKey(Constants.ORDER_CONFIRM_KEY_PREFIX, Constants.ORDER), txId)
                || distributedCacheService.inSet(Constants.getKey(Constants.ORDER_CANCEL_KEY_PREFIX, Constants.ORDER), txId)) {
            log.warn("placeOrder|基于Lua脚本实现库存防超卖-提交订单confirm或cancel方法已执行过|{}", txId);
            throw new BusinessException(ErrorCodeEnum.OPERATION_ERROR, "订单TCC事务已结束");
        }

        // 获取商品
        SeckillGoodsDTO seckillGoodsDTO = seckillGoodsDubboService.getSeckillGoodsDTO(seckillOrderCommand.getGoodsId(), seckillOrderCommand.getVersion());
        // 校验商品状态
        this.checkSeckillGoods(seckillOrderCommand, seckillGoodsDTO);

        String cacheKey = Constants.getKey(Constants.SECKILL_GOODS_STOCK_CACHE_KEY, String.valueOf(seckillOrderCommand.getGoodsId()));
        boolean isDecrementCachedStock = false;
        boolean isTryRecorded = false;
        try {
            Long result = distributedCacheService.decrementByLua(cacheKey, seckillOrderCommand.getQuantity());
            distributedCacheService.checkLuaResult(result);
            isDecrementCachedStock = true;
            // 构建订单
            SeckillOrder seckillOrder = this.buildSeckillOrder(userId, seckillOrderCommand, seckillGoodsDTO);
            seckillOrder.setId(txId);
            // 保存订单
            seckillOrderDomainService.saveSeckillOrder(seckillOrder);
            // 保存try日志
            distributedCacheService.addSet(tryKey, txId);
            isTryRecorded = true;
            // 扣减数据库库存
            if (!seckillGoodsDubboService.decreaseAvailableStock(seckillOrderCommand.getGoodsId(), seckillOrderCommand.getQuantity(), txId)) {
                throw new BusinessException(ErrorCodeEnum.FORBIDDEN_ERROR, "库存不足");
            }
            return seckillOrder.getId();
        } catch (Exception e) {
            if (isTryRecorded) {
                distributedCacheService.removeSet(tryKey, txId);
            }
            if (isDecrementCachedStock) {
                distributedCacheService.incrementByLua(cacheKey, seckillOrderCommand.getQuantity());
            }

            throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, e.getMessage());
        }
    }

}
