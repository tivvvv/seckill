package com.tiv.seckill.application.order.place.impl;

import com.alibaba.fastjson.JSONObject;
import com.tiv.seckill.application.command.SeckillOrderCommand;
import com.tiv.seckill.application.order.place.SeckillPlaceOrderService;
import com.tiv.seckill.application.service.SeckillGoodsService;
import com.tiv.seckill.domain.code.ErrorCodeEnum;
import com.tiv.seckill.domain.constants.Constants;
import com.tiv.seckill.domain.dto.SeckillGoodsDTO;
import com.tiv.seckill.domain.exception.BusinessException;
import com.tiv.seckill.domain.model.SeckillOrder;
import com.tiv.seckill.domain.service.SeckillOrderDomainService;
import com.tiv.seckill.infra.cache.distributed.DistributedCacheService;
import com.tiv.seckill.infra.lock.DistributedLock;
import com.tiv.seckill.infra.lock.DistributedLockFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@ConditionalOnProperty(name = "order.place.type", havingValue = "lock")
public class SeckillPlaceOrderLockServiceImpl implements SeckillPlaceOrderService {

    @Autowired
    private SeckillGoodsService seckillGoodsService;

    @Autowired
    private SeckillOrderDomainService seckillOrderDomainService;

    @Autowired
    private DistributedLockFactory distributedLockFactory;

    @Autowired
    private DistributedCacheService distributedCacheService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long placeOrder(Long userId, SeckillOrderCommand seckillOrderCommand) {
        // 获取商品
        SeckillGoodsDTO seckillGoodsDTO = seckillGoodsService.getSeckillGoodsDTO(seckillOrderCommand.getGoodsId(), seckillOrderCommand.getVersion());
        // 校验商品状态
        this.checkSeckillGoods(seckillOrderCommand, seckillGoodsDTO);

        String lockKey = Constants.getKey(Constants.ORDER_LOCK_KEY_PREFIX, String.valueOf(seckillOrderCommand.getGoodsId()));
        DistributedLock distributedLock = distributedLockFactory.getDistributedLock(lockKey);

        String cacheKey = Constants.getKey(Constants.SECKILL_GOODS_STOCK_CACHE_KEY, String.valueOf(seckillOrderCommand.getGoodsId()));

        boolean isDecrementCachedStock = false;
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
            // 保存订单
            seckillOrderDomainService.saveSeckillOrder(seckillOrder);
            // 扣减数据库库存
            seckillGoodsService.decreaseAvailableDbStock(seckillOrderCommand.getGoodsId(), seckillOrderCommand.getQuantity());
            return seckillOrder.getId();
        } catch (Exception e) {
            if (isDecrementCachedStock) {
                distributedCacheService.increment(cacheKey, Long.valueOf(seckillOrderCommand.getQuantity()));
            }
            if (e instanceof InterruptedException) {
                log.error("SeckillPlaceOrderLockServiceImpl--placeOrder|分布式锁被中断|参数:{}", JSONObject.toJSONString(seckillOrderCommand), e);
            } else {
                log.error("SeckillPlaceOrderLockServiceImpl--placeOrder|下单失败|参数:{}", JSONObject.toJSONString(seckillOrderCommand), e);
            }
            throw new BusinessException(ErrorCodeEnum.SYSTEM_ERROR, e.getMessage());
        } finally {
            distributedLock.unLock();
        }
    }

}