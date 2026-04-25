package com.tiv.seckill.goods.application.dubbo;

import com.tiv.seckill.common.cache.distributed.DistributedCacheService;
import com.tiv.seckill.common.constants.Constants;
import com.tiv.seckill.common.exception.BusinessException;
import com.tiv.seckill.common.exception.ErrorCodeEnum;
import com.tiv.seckill.common.model.dto.SeckillGoodsDTO;
import com.tiv.seckill.dubbo.interfaces.goods.SeckillGoodsDubboService;
import com.tiv.seckill.goods.application.service.SeckillGoodsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.hmily.annotation.HmilyTCC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@DubboService(version = "1.0.0")
public class SeckillGoodsDubboServiceImpl implements SeckillGoodsDubboService {

    @Autowired
    private SeckillGoodsService seckillGoodsService;

    @Autowired
    private DistributedCacheService distributedCacheService;

    @Override
    public SeckillGoodsDTO getSeckillGoodsDTO(Long id, Long version) {
        return seckillGoodsService.getSeckillGoodsDTO(id, version);
    }

    @Override
    public boolean decreaseAvailableDbStock(Long id, Integer count) {
        return seckillGoodsService.decreaseAvailableDbStock(id, count);
    }

    @Override
    @HmilyTCC(confirmMethod = "confirmMethod", cancelMethod = "cancelMethod")
    public boolean decreaseAvailableStock(Long id, Integer count, Long txId) {
        String tryKey = Constants.getKey(Constants.ORDER_TRY_KEY_PREFIX, Constants.GOODS);
        if (distributedCacheService.inSet(tryKey, txId)) {
            log.warn("decreaseAvailableStock|扣减库存try方法已执行过|{}", txId);
            return false;
        }
        if (distributedCacheService.inSet(Constants.getKey(Constants.ORDER_CONFIRM_KEY_PREFIX, Constants.GOODS), txId)
                || distributedCacheService.inSet(Constants.getKey(Constants.ORDER_CANCEL_KEY_PREFIX, Constants.GOODS), txId)) {
            log.warn("decreaseAvailableStock|扣减库存confirm或cancel方法已执行过|{}", txId);
            return false;
        }
        boolean isTryRecorded = false;
        try {
            distributedCacheService.addSet(tryKey, txId);
            isTryRecorded = true;
            return seckillGoodsService.decreaseAvailableStock(id, count);
        } catch (Exception e) {
            log.error("decreaseAvailableStock|扣减库存try方法执行失败|{}", txId, e);
            if (isTryRecorded) {
                distributedCacheService.removeSet(tryKey, txId);
            }
            throw new BusinessException(ErrorCodeEnum.FORBIDDEN_ERROR, "库存不足");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean confirmMethod(Long id, Integer count, Long txId) {
        if (!distributedCacheService.inSet(Constants.getKey(Constants.ORDER_TRY_KEY_PREFIX, Constants.GOODS), txId)) {
            log.warn("confirmMethod|扣减库存try方法执行失败|{}", txId);
            return false;
        }
        String confirmKey = Constants.getKey(Constants.ORDER_CONFIRM_KEY_PREFIX, Constants.GOODS);
        if (distributedCacheService.inSet(confirmKey, txId)) {
            log.warn("confirmMethod|扣减库存confirm方法已执行过|{}", txId);
            return false;
        }

        log.info("confirmMethod|扣减库存confirm方法开始执行|{}", txId);
        try {
            distributedCacheService.addSet(confirmKey, txId);
            return true;
        } catch (Exception e) {
            log.error("confirmMethod|扣减库存confirm方法执行失败|{}", txId, e);
            distributedCacheService.removeSet(confirmKey, txId);
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean cancelMethod(Long id, Integer count, Long txId) {
        if (!distributedCacheService.inSet(Constants.getKey(Constants.ORDER_TRY_KEY_PREFIX, Constants.GOODS), txId)) {
            log.warn("cancelMethod|扣减库存try方法执行失败|{}", txId);
            return false;
        }
        String cancelKey = Constants.getKey(Constants.ORDER_CANCEL_KEY_PREFIX, Constants.GOODS);
        if (distributedCacheService.inSet(cancelKey, txId)) {
            log.warn("cancelMethod|扣减库存cancel方法已执行过|{}", txId);
            return false;
        }

        log.info("cancelMethod|扣减库存cancel方法开始执行|{}", txId);
        boolean isCancelRecorded = false;
        try {
            distributedCacheService.addSet(cancelKey, txId);
            isCancelRecorded = true;
            return seckillGoodsService.increaseAvailableStock(id, count);
        } catch (Exception e) {
            log.error("cancelMethod|扣减库存cancel方法执行失败|{}", txId, e);
            if (isCancelRecorded) {
                distributedCacheService.removeSet(cancelKey, txId);
            }
        }
        return false;
    }

}