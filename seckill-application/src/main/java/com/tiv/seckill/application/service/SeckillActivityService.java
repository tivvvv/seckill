package com.tiv.seckill.application.service;

import com.tiv.seckill.domain.dto.SeckillActivityDTO;
import com.tiv.seckill.domain.model.SeckillActivity;

import java.util.Date;
import java.util.List;

public interface SeckillActivityService {

    void saveSeckillActivity(SeckillActivityDTO seckillActivityDTO);

    void updateStatus(Long id, Integer status);

    List<SeckillActivity> getSeckillActivityDTOList(Integer status);

    List<SeckillActivity> getSeckillActivityListByNow(Date currentTime, Integer status);

    SeckillActivity getSeckillActivityById(Long id);

    List<SeckillActivityDTO> getSeckillActivityDTOList(Integer status, Long version);

    SeckillActivityDTO getSeckillActivityDTO(Long id, Long version);

}