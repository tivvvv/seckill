package com.tiv.seckill.goods.infra.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tiv.seckill.common.exception.BusinessException;
import com.tiv.seckill.common.exception.ErrorCodeEnum;
import com.tiv.seckill.goods.domain.model.SeckillGoods;
import com.tiv.seckill.goods.domain.repository.SeckillGoodsRepository;
import com.tiv.seckill.goods.infra.mapper.SeckillGoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SeckillGoodsRepositoryImpl implements SeckillGoodsRepository {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Override
    public int saveSeckillGoods(SeckillGoods seckillGoods) {
        if (seckillGoods == null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "seckillGoods 为 null");
        }
        return seckillGoodsMapper.insert(seckillGoods);
    }

    @Override
    public int updateStatus(Long id, Integer status) {
        SeckillGoods seckillGoods = SeckillGoods.builder()
                .id(id)
                .status(status)
                .build();
        return seckillGoodsMapper.updateById(seckillGoods);
    }

    @Override
    public int decreaseAvailableStock(Long id, Integer count) {
        return seckillGoodsMapper.decreaseAvailableStock(id, count);
    }

    @Override
    public SeckillGoods getSeckillGoodsById(Long id) {
        return seckillGoodsMapper.selectById(id);
    }

    @Override
    public List<SeckillGoods> getSeckillGoodsListByActivityId(Long activityId) {
        LambdaQueryWrapper<SeckillGoods> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SeckillGoods::getActivityId, activityId);
        return seckillGoodsMapper.selectList(queryWrapper);
    }

    @Override
    public Integer getAvailableStockById(Long id) {
        SeckillGoods seckillGoods = getSeckillGoodsById(id);
        return seckillGoods != null ? seckillGoods.getAvailableStock() : null;
    }

}