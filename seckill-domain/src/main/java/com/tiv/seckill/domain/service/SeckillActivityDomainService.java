package com.tiv.seckill.domain.service;

import com.tiv.seckill.domain.model.SeckillActivity;

import java.util.Date;
import java.util.List;

public interface SeckillActivityDomainService {

    void saveSeckillActivity(SeckillActivity seckillActivity);

    void updateStatus(Long id, Integer status);

    List<SeckillActivity> getSeckillActivityList(Integer status);

    List<SeckillActivity> getSeckillActivityListByNow(Date currentTime, Integer status);

    SeckillActivity getSeckillActivityById(Long id);

}