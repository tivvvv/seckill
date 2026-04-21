package com.tiv.seckill.common.event.publisher;

import com.alibaba.cola.event.DomainEventI;
import com.alibaba.cola.event.EventBusI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocalDomainEventPublisher implements EventPublisher {

    @Autowired
    private EventBusI eventBus;

    @Override
    public void publish(DomainEventI domainEvent) {
        eventBus.fire(domainEvent);
    }

}