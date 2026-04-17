package com.tiv.seckill.domain.service.impl;

import com.tiv.seckill.domain.code.ErrorCodeEnum;
import com.tiv.seckill.domain.enums.SeckillGoodsStatusEnum;
import com.tiv.seckill.domain.event.SeckillGoodsEvent;
import com.tiv.seckill.domain.event.publisher.EventPublisher;
import com.tiv.seckill.domain.exception.BusinessException;
import com.tiv.seckill.domain.model.SeckillGoods;
import com.tiv.seckill.domain.repository.SeckillGoodsRepository;
import com.tiv.seckill.domain.service.SeckillGoodsDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SeckillGoodsDomainServiceImpl implements SeckillGoodsDomainService {

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private SeckillGoodsRepository seckillGoodsRepository;

    @Override
    public void saveSeckillGoods(SeckillGoods seckillGoods) {
        if (seckillGoods == null || !seckillGoods.validateParams()) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "秒杀商品参数异常");
        }
        seckillGoods.setStatus(SeckillGoodsStatusEnum.PUBLISHED.getCode());
        seckillGoodsRepository.saveSeckillGoods(seckillGoods);

        SeckillGoodsEvent seckillGoodsEvent = new SeckillGoodsEvent(seckillGoods.getId(), seckillGoods.getStatus(), seckillGoods.getActivityId());
        eventPublisher.publish(seckillGoodsEvent);
        log.info("goodsPublish|发布秒杀商品事件发布成功|{}", seckillGoodsEvent.getId());
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        if (id == null || status == null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "秒杀商品参数异常");
        }
        SeckillGoods seckillGoods = seckillGoodsRepository.getSeckillGoodsById(id);
        if (seckillGoods == null) {
            throw new BusinessException(ErrorCodeEnum.NOT_FOUND_ERROR, "秒杀商品不存在");
        }
        seckillGoodsRepository.updateStatus(id, status);

        SeckillGoodsEvent seckillGoodsEvent = new SeckillGoodsEvent(id, status, seckillGoods.getActivityId());
        eventPublisher.publish(seckillGoodsEvent);
        log.info("goodsPublish|更新秒杀商品状态事件发布成功|{}", seckillGoodsEvent.getId());
    }

    @Override
    public boolean decreaseAvailableStock(Long id, Integer count) {
        if (id == null || count == null || count <= 0) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "秒杀商品参数异常");
        }
        SeckillGoods seckillGoods = seckillGoodsRepository.getSeckillGoodsById(id);
        if (seckillGoods == null) {
            throw new BusinessException(ErrorCodeEnum.NOT_FOUND_ERROR, "秒杀商品不存在");
        }
        boolean updateSuccess = seckillGoodsRepository.decreaseAvailableStock(id, count) > 0;

        if (updateSuccess) {
            SeckillGoodsEvent seckillGoodsEvent = new SeckillGoodsEvent(id, seckillGoods.getStatus(), seckillGoods.getActivityId());
            eventPublisher.publish(seckillGoodsEvent);
            log.info("goodsPublish|扣减秒杀商品库存事件发布成功|{}", id);
        } else {
            log.info("goodsPublish|秒杀商品库存未扣减|{}", id);
        }
        return updateSuccess;
    }

    @Override
    public boolean decreaseAvailableDbStock(Long id, Integer count) {
        if (id == null || count == null || count <= 0) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "秒杀商品参数异常");
        }
        return seckillGoodsRepository.decreaseAvailableStock(id, count) > 0;
    }

    @Override
    public SeckillGoods getSeckillGoodsById(Long id) {
        return seckillGoodsRepository.getSeckillGoodsById(id);
    }

    @Override
    public List<SeckillGoods> getSeckillGoodsListByActivityId(Long activityId) {
        return seckillGoodsRepository.getSeckillGoodsListByActivityId(activityId);
    }

    @Override
    public Integer getAvailableStockById(Long id) {
        return seckillGoodsRepository.getAvailableStockById(id);
    }

}