
package com.tiv.seckill.application.builder;

import com.tiv.seckill.application.builder.common.SeckillCommonBuilder;
import com.tiv.seckill.domain.dto.SeckillActivityDTO;
import com.tiv.seckill.domain.model.SeckillActivity;
import com.tiv.seckill.infra.util.bean.BeanUtil;

public class SeckillActivityBuilder extends SeckillCommonBuilder {

    public static SeckillActivityDTO toSeckillActivityDTO(SeckillActivity seckillActivity) {
        if (seckillActivity == null) {
            return null;
        }
        SeckillActivityDTO seckillActivityDTO = new SeckillActivityDTO();
        BeanUtil.copyProperties(seckillActivity, seckillActivityDTO);
        return seckillActivityDTO;
    }

}