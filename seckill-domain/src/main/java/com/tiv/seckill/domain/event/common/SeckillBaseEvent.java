package com.tiv.seckill.domain.event.common;

import com.alibaba.cola.event.DomainEventI;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SeckillBaseEvent implements DomainEventI {

    private Long id;

    private Integer status;

}