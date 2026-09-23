package com.nexo.ecommerce.orders.application.ports;

import com.nexo.ecommerce.orders.domain.events.OrderCreatedEvent;

public interface EventPublisherPort {
    
    public void publishOrderCreatedEvent(OrderCreatedEvent orderStatusEvent);


}
