package com.tiv.seckill.domain.event.publisher;

import com.alibaba.cola.event.DomainEventI;

public interface EventPublisher {

    void publish(DomainEventI domainEvent);

}