package com.tiv.seckill.order.application.place.impl;

import com.tiv.seckill.common.cache.distributed.DistributedCacheService;
import com.tiv.seckill.common.constants.Constants;
import com.tiv.seckill.dubbo.interfaces.goods.SeckillGoodsDubboService;
import com.tiv.seckill.order.application.command.SeckillOrderCommand;
import com.tiv.seckill.order.domain.service.SeckillOrderDomainService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class SeckillPlaceOrderBaseServiceImpl {

    @DubboReference(version = "1.0.0")
    protected SeckillGoodsDubboService seckillGoodsDubboService;

    @Autowired
    protected SeckillOrderDomainService seckillOrderDomainService;

    @Autowired
    protected DistributedCacheService distributedCacheService;

    @Transactional(rollbackFor = Exception.class)
    public Long confirmMethod(Long userId, SeckillOrderCommand seckillOrderCommand, Long txId) {
        String tryKey = Constants.getKey(Constants.ORDER_TRY_KEY_PREFIX, Constants.ORDER);
        if (!distributedCacheService.inSet(tryKey, txId)) {
            log.warn("confirmMethod|提交订单try方法执行失败|{}", txId);
            return txId;
        }
        String confirmKey = Constants.getKey(Constants.ORDER_CONFIRM_KEY_PREFIX, Constants.ORDER);
        if (distributedCacheService.inSet(confirmKey, txId)) {
            log.warn("confirmMethod|提交订单confirm方法已执行过|{}", txId);
            return txId;
        }

        log.info("confirmMethod|提交订单confirm方法开始执行|{}", txId);
        try {
            distributedCacheService.addSet(confirmKey, txId);
        } catch (Exception e) {
            log.error("confirmMethod|提交订单confirm方法执行失败|{}", txId, e);
            distributedCacheService.removeSet(confirmKey, txId);
        }
        return txId;
    }

    @Transactional(rollbackFor = Exception.class)
    public Long cancelMethod(Long userId, SeckillOrderCommand seckillOrderCommand, Long txId) {
        String tryKey = Constants.getKey(Constants.ORDER_TRY_KEY_PREFIX, Constants.ORDER);
        String cancelKey = Constants.getKey(Constants.ORDER_CANCEL_KEY_PREFIX, Constants.ORDER);
        if (distributedCacheService.inSet(cancelKey, txId)) {
            log.warn("cancelMethod|提交订单cancel方法已执行过|{}", txId);
            return txId;
        }
        if (!distributedCacheService.inSet(tryKey, txId)) {
            log.warn("cancelMethod|提交订单空回滚|{}", txId);
            distributedCacheService.addSet(cancelKey, txId);
            return txId;
        }

        log.info("cancelMethod|提交订单cancel方法开始执行|{}", txId);
        boolean isCancelRecorded = false;
        try {
            distributedCacheService.addSet(cancelKey, txId);
            isCancelRecorded = true;
            seckillOrderDomainService.deleteSeckillOrder(txId);
        } catch (Exception e) {
            if (isCancelRecorded) {
                distributedCacheService.removeSet(cancelKey, txId);
            }
        }
        return txId;
    }

}
