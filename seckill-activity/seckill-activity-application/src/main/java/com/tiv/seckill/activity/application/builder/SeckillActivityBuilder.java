
package com.tiv.seckill.activity.application.builder;

import com.tiv.seckill.activity.domain.model.SeckillActivity;
import com.tiv.seckill.common.builder.SeckillCommonBuilder;
import com.tiv.seckill.common.model.dto.SeckillActivityDTO;
import com.tiv.seckill.common.util.bean.BeanUtil;

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