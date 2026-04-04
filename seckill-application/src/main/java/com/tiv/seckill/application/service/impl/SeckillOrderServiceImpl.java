package com.tiv.seckill.application.service.impl;

import com.tiv.seckill.application.service.SeckillGoodsService;
import com.tiv.seckill.application.service.SeckillOrderService;
import com.tiv.seckill.domain.code.ErrorCodeEnum;
import com.tiv.seckill.domain.dto.SeckillOrderDTO;
import com.tiv.seckill.domain.enums.SeckillGoodsStatusEnum;
import com.tiv.seckill.domain.enums.SeckillOrderStatusEnum;
import com.tiv.seckill.domain.exception.BusinessException;
import com.tiv.seckill.domain.model.SeckillGoods;
import com.tiv.seckill.domain.model.SeckillOrder;
import com.tiv.seckill.domain.repository.SeckillOrderRepository;
import com.tiv.seckill.infra.util.bean.BeanUtil;
import com.tiv.seckill.infra.util.id.SnowFlakeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private SeckillGoodsService seckillGoodsService;

    @Autowired
    private SeckillOrderRepository seckillOrderRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveSeckillOrder(SeckillOrderDTO seckillOrderDTO) {
        if (seckillOrderDTO == null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "seckillOrderDTO 为 null");
        }
        // 获取商品
        SeckillGoods seckillGoods = seckillGoodsService.getSeckillGoodsById(seckillOrderDTO.getGoodsId());

        // 商品不存在
        if (seckillGoods == null) {
            throw new BusinessException(ErrorCodeEnum.NOT_FOUND_ERROR, "商品不存在");
        }
        // 商品未上线
        if (SeckillGoodsStatusEnum.PUBLISHED.getCode().equals(seckillGoods.getStatus())) {
            throw new BusinessException(ErrorCodeEnum.FORBIDDEN_ERROR, "商品未上线");
        }
        // 商品已下架
        if (SeckillGoodsStatusEnum.OFFLINE.getCode().equals(seckillGoods.getStatus())) {
            throw new BusinessException(ErrorCodeEnum.FORBIDDEN_ERROR, "商品已下架");
        }

        // 触发限购
        if (seckillOrderDTO.getQuantity() > seckillGoods.getLimitNum()) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "下单数量超过限购数量");
        }

        // 库存不足
        if (seckillGoods.getAvailableStock() == null
                || seckillGoods.getAvailableStock() <= 0
                || seckillOrderDTO.getQuantity() > seckillGoods.getAvailableStock()) {
            throw new BusinessException(ErrorCodeEnum.FORBIDDEN_ERROR, "库存不足");
        }

        SeckillOrder seckillOrder = new SeckillOrder();
        BeanUtil.copyProperties(seckillOrderDTO, seckillOrder);
        seckillOrder.setId(SnowFlakeFactory.getSnowFlakeFromCache().nextId());
        seckillOrder.setGoodsName(seckillGoods.getGoodsName());
        seckillOrder.setActivityPrice(seckillGoods.getActivityPrice());
        seckillOrder.setOrderPrice(seckillGoods.getActivityPrice().multiply(BigDecimal.valueOf(seckillOrder.getQuantity())));
        seckillOrder.setStatus(SeckillOrderStatusEnum.CREATED.getCode());
        seckillOrder.setCreateTime(new Date());

        // 扣减库存
        seckillGoodsService.decreaseAvailableStock(seckillOrder.getGoodsId(), seckillOrder.getQuantity());

        // 保存订单
        return seckillOrderRepository.saveSeckillOrder(seckillOrder);
    }

    @Override
    public List<SeckillOrder> getSeckillOrderByUserId(Long userId) {
        return seckillOrderRepository.getSeckillOrderByUserId(userId);
    }

    @Override
    public List<SeckillOrder> getSeckillOrderByActivityId(Long activityId) {
        return seckillOrderRepository.getSeckillOrderByActivityId(activityId);
    }

}