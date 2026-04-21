package com.tiv.seckill.common.event.publisher;

import com.alibaba.cola.event.DomainEventI;

public interface EventPublisher {

    void publish(DomainEventI domainEvent);

}