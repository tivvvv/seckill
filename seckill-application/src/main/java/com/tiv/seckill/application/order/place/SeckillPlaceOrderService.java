package com.tiv.seckill.application.order.place;

import com.tiv.seckill.application.command.SeckillOrderCommand;
import com.tiv.seckill.domain.code.ErrorCodeEnum;
import com.tiv.seckill.domain.dto.SeckillGoodsDTO;
import com.tiv.seckill.domain.enums.SeckillGoodsStatusEnum;
import com.tiv.seckill.domain.enums.SeckillOrderStatusEnum;
import com.tiv.seckill.domain.exception.BusinessException;
import com.tiv.seckill.domain.model.SeckillOrder;
import com.tiv.seckill.infra.util.bean.BeanUtil;
import com.tiv.seckill.infra.util.id.SnowFlakeFactory;

import java.math.BigDecimal;
import java.util.Date;

public interface SeckillPlaceOrderService {

    /**
     * 下单操作
     *
     * @param userId
     * @param seckillOrderCommand
     * @return
     */
    Long placeOrder(Long userId, SeckillOrderCommand seckillOrderCommand);

    default SeckillOrder buildSeckillOrder(Long userId, SeckillOrderCommand seckillOrderCommand, SeckillGoodsDTO seckillGoodsDTO) {
        SeckillOrder seckillOrder = new SeckillOrder();
        BeanUtil.copyProperties(seckillOrderCommand, seckillOrder);
        BigDecimal orderPrice = seckillGoodsDTO.getActivityPrice().multiply(BigDecimal.valueOf(seckillOrder.getQuantity()));

        seckillOrder.setId(SnowFlakeFactory.getSnowFlakeFromCache().nextId())
                .setGoodsName(seckillGoodsDTO.getGoodsName())
                .setUserId(userId)
                .setActivityPrice(seckillGoodsDTO.getActivityPrice())
                .setOrderPrice(orderPrice)
                .setStatus(SeckillOrderStatusEnum.CREATED.getCode())
                .setCreateTime(new Date());
        return seckillOrder;
    }

    default void checkSeckillGoods(SeckillOrderCommand seckillOrderCommand, SeckillGoodsDTO seckillGoodsDTO) {
        // 商品不存在
        if (seckillGoodsDTO == null) {
            throw new BusinessException(ErrorCodeEnum.NOT_FOUND_ERROR, "商品不存在");
        }
        // 商品未上线
        if (SeckillGoodsStatusEnum.PUBLISHED.getCode().equals(seckillGoodsDTO.getStatus())) {
            throw new BusinessException(ErrorCodeEnum.FORBIDDEN_ERROR, "商品未上线");
        }
        // 商品已下架
        if (SeckillGoodsStatusEnum.OFFLINE.getCode().equals(seckillGoodsDTO.getStatus())) {
            throw new BusinessException(ErrorCodeEnum.FORBIDDEN_ERROR, "商品已下架");
        }

        // 触发限购
        if (seckillOrderCommand.getQuantity() > seckillGoodsDTO.getLimitNum()) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "下单数量超过限购数量");
        }

        // 库存不足
        if (seckillGoodsDTO.getAvailableStock() == null
                || seckillGoodsDTO.getAvailableStock() <= 0
                || seckillOrderCommand.getQuantity() > seckillGoodsDTO.getAvailableStock()) {
            throw new BusinessException(ErrorCodeEnum.FORBIDDEN_ERROR, "库存不足");
        }
    }

}