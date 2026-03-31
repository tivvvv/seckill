package com.tiv.seckill.domain.repository;

import com.tiv.seckill.domain.model.SeckillActivity;

import java.util.Date;
import java.util.List;

public interface SeckillActivityRepository {

    int saveSeckillActivity(SeckillActivity seckillActivity);

    int updateStatus(Long id, Integer status);

    List<SeckillActivity> getSeckillActivityList(Integer status);

    List<SeckillActivity> getSeckillActivityListByNow(Date currentTime, Integer status);

    SeckillActivity getSeckillActivityById(Long id);

}