package com.tiv.seckill.common.event;

import com.alibaba.cola.event.DomainEventI;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SeckillBaseEvent implements DomainEventI {

    private Long id;

    private Integer status;

}