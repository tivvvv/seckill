package com.tiv.seckill.activity.infra.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tiv.seckill.activity.domain.model.SeckillActivity;
import com.tiv.seckill.activity.domain.repository.SeckillActivityRepository;
import com.tiv.seckill.activity.infra.mapper.SeckillActivityMapper;
import com.tiv.seckill.common.exception.BusinessException;
import com.tiv.seckill.common.exception.ErrorCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class SeckillActivityRepositoryImpl implements SeckillActivityRepository {

    @Autowired
    private SeckillActivityMapper seckillActivityMapper;

    @Override
    public int saveSeckillActivity(SeckillActivity seckillActivity) {
        if (seckillActivity == null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "seckillActivity 为 null");
        }
        return seckillActivityMapper.insert(seckillActivity);
    }

    @Override
    public int updateStatus(Long id, Integer status) {
        SeckillActivity seckillActivity = SeckillActivity.builder()
                .id(id)
                .status(status)
                .build();
        return seckillActivityMapper.updateById(seckillActivity);
    }

    @Override
    public List<SeckillActivity> getSeckillActivityList(Integer status) {
        LambdaQueryWrapper<SeckillActivity> queryWrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            queryWrapper.eq(SeckillActivity::getStatus, status);
        }
        return seckillActivityMapper.selectList(queryWrapper);
    }

    @Override
    public List<SeckillActivity> getSeckillActivityListByNow(Date currentTime, Integer status) {
        if (currentTime == null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "currentTime 为 null");
        }

        LambdaQueryWrapper<SeckillActivity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.le(SeckillActivity::getStartTime, currentTime)
                .ge(SeckillActivity::getEndTime, currentTime);
        if (status != null) {
            queryWrapper.eq(SeckillActivity::getStatus, status);
        }
        return seckillActivityMapper.selectList(queryWrapper);
    }

    @Override
    public SeckillActivity getSeckillActivityById(Long id) {
        return seckillActivityMapper.selectById(id);
    }

}