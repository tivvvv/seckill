package com.tiv.seckill.order.application.place.impl;

import com.alibaba.fastjson2.JSON;
import com.tiv.seckill.common.constants.Constants;
import com.tiv.seckill.common.exception.BusinessException;
import com.tiv.seckill.common.exception.ErrorCodeEnum;
import com.tiv.seckill.common.lock.DistributedLock;
import com.tiv.seckill.common.lock.DistributedLockFactory;
import com.tiv.seckill.common.model.dto.SeckillGoodsDTO;
import com.tiv.seckill.order.application.command.SeckillOrderCommand;
import com.tiv.seckill.order.application.place.SeckillPlaceOrderService;
import com.tiv.seckill.order.domain.model.SeckillOrder;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hmily.annotation.HmilyTCC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@ConditionalOnProperty(name = "order.place.type", havingValue = "lock")
public class SeckillPlaceOrderLockServiceImpl extends SeckillPlaceOrderBaseServiceImpl implements SeckillPlaceOrderService {

    @Autowired
    private DistributedLockFactory distributedLockFactory;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @HmilyTCC(confirmMethod = "confirmMethod", cancelMethod = "cancelMethod")
    public Long placeOrder(Long userId, SeckillOrderCommand seckillOrderCommand, Long txId) {
        String tryKey = Constants.getKey(Constants.ORDER_TRY_KEY_PREFIX, Constants.ORDER);
        // 幂等处理
        if (distributedCacheService.inSet(tryKey, txId)) {
            log.warn("placeOrder|基于分布式锁实现库存防超卖-提交订单ry方法已执行过|{}", txId);
            return txId;
        }
        // 悬挂处理
        if (distributedCacheService.inSet(Constants.getKey(Constants.ORDER_CONFIRM_KEY_PREFIX, Constants.ORDER), txId)
                || distributedCacheService.inSet(Constants.getKey(Constants.ORDER_CANCEL_KEY_PREFIX, Constants.ORDER), txId)) {
            log.warn("placeOrder|基于分布式锁实现库存防超卖-提交订单confirm或cancel方法已执行过|{}", txId);
            return txId;
        }

        // 获取商品
        SeckillGoodsDTO seckillGoodsDTO = seckillGoodsDubboService.getSeckillGoodsDTO(seckillOrderCommand.getGoodsId(), seckillOrderCommand.getVersion());
        // 校验商品状态
        this.checkSeckillGoods(seckillOrderCommand, seckillGoodsDTO);

        String lockKey = Constants.getKey(Constants.ORDER_LOCK_KEY_PREFIX, String.valueOf(seckillOrderCommand.getGoodsId()));
        DistributedLock distributedLock = distributedLockFactory.getDistributedLock(lockKey);

        String cacheKey = Constants.getKey(Constants.SECKILL_GOODS_STOCK_CACHE_KEY, String.valueOf(seckillOrderCommand.getGoodsId()));

        boolean isDecrementCachedStock = false;
        boolean isTryRecorded = false;
        try {
            // 尝试获取分布式锁
            if (!distributedLock.tryLock(2L, 5L, TimeUnit.SECONDS)) {
                throw new BusinessException(ErrorCodeEnum.RETRY_LATER);
            }
            // 获取库存
            Integer stock = distributedCacheService.getObject(cacheKey, Integer.class);
            if (stock < seckillOrderCommand.getQuantity()) {
                throw new BusinessException(ErrorCodeEnum.FORBIDDEN_ERROR, "库存不足");
            }
            // 扣减缓存库存
            distributedCacheService.decrement(cacheKey, Long.valueOf(seckillOrderCommand.getQuantity()));
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
            if (isDecrementCachedStock) {
                distributedCacheService.increment(cacheKey, Long.valueOf(seckillOrderCommand.getQuantity()));
            }
            if (isTryRecorded) {
                distributedCacheService.removeSet(tryKey, txId);
            }
            if (e instanceof InterruptedException) {
                log.error("SeckillPlaceOrderLockServiceImpl--placeOrder|分布式锁被中断|参数:{}", JSON.toJSONString(seckillOrderCommand), e);
            } else {
                log.error("SeckillPlaceOrderLockServiceImpl--placeOrder|下单失败|参数:{}", JSON.toJSONString(seckillOrderCommand), e);
            }
            throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, e.getMessage());
        } finally {
            distributedLock.unLock();
        }
    }

}
