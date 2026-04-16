package com.tiv.seckill.infra.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tiv.seckill.domain.code.ErrorCodeEnum;
import com.tiv.seckill.domain.exception.BusinessException;
import com.tiv.seckill.domain.model.SeckillOrder;
import com.tiv.seckill.domain.repository.SeckillOrderRepository;
import com.tiv.seckill.infra.mapper.SeckillOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SeckillOrderRepositoryImpl implements SeckillOrderRepository {

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Override
    public boolean saveSeckillOrder(SeckillOrder seckillOrder) {
        if (seckillOrder == null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "seckillOrder 为 null");
        }
        return seckillOrderMapper.insert(seckillOrder) == 1;
    }

    @Override
    public List<SeckillOrder> getSeckillOrderByUserId(Long userId) {
        LambdaQueryWrapper<SeckillOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SeckillOrder::getUserId, userId);
        return seckillOrderMapper.selectList(queryWrapper);
    }

    @Override
    public List<SeckillOrder> getSeckillOrderByActivityId(Long activityId) {
        LambdaQueryWrapper<SeckillOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SeckillOrder::getActivityId, activityId);
        return seckillOrderMapper.selectList(queryWrapper);
    }

}