package com.tiv.seckill.application.service.impl;

import com.tiv.seckill.application.service.SeckillActivityService;
import com.tiv.seckill.domain.code.ErrorCodeEnum;
import com.tiv.seckill.domain.dto.SeckillActivityDTO;
import com.tiv.seckill.domain.enums.SeckillActivityStatusEnum;
import com.tiv.seckill.domain.exception.BusinessException;
import com.tiv.seckill.domain.model.SeckillActivity;
import com.tiv.seckill.domain.repository.SeckillActivityRepository;
import com.tiv.seckill.infra.util.bean.BeanUtil;
import com.tiv.seckill.infra.util.id.SnowFlakeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SeckillActivityServiceImpl implements SeckillActivityService {

    @Autowired
    private SeckillActivityRepository seckillActivityRepository;

    @Override
    public void saveSeckillActivity(SeckillActivityDTO seckillActivityDTO) {
        if (seckillActivityDTO == null) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "seckillActivityDTO 为 null");
        }
        SeckillActivity seckillActivity = new SeckillActivity();
        BeanUtil.copyProperties(seckillActivityDTO, seckillActivity);
        seckillActivity.setId(SnowFlakeFactory.getSnowFlakeFromCache().nextId());
        seckillActivity.setStatus(SeckillActivityStatusEnum.PUBLISHED.getCode());

        seckillActivityRepository.saveSeckillActivity(seckillActivity);
    }

    @Override
    public int updateStatus(Long id, Integer status) {
        return seckillActivityRepository.updateStatus(id, status);
    }

    @Override
    public List<SeckillActivity> getSeckillActivityList(Integer status) {
        return seckillActivityRepository.getSeckillActivityList(status);
    }

    @Override
    public List<SeckillActivity> getSeckillActivityListByNow(Date currentTime, Integer status) {
        return seckillActivityRepository.getSeckillActivityListByNow(currentTime, status);
    }

    @Override
    public SeckillActivity getSeckillActivityById(Long id) {
        return seckillActivityRepository.getSeckillActivityById(id);
    }

}