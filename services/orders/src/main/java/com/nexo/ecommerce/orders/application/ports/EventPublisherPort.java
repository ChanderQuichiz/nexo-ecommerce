package com.nexo.ecommerce.orders.application.ports;

import com.nexo.ecommerce.orders.domain.events.OrderCreatedEvent;
import com.nexo.ecommerce.orders.domain.events.OrderPaidEvent;
import com.nexo.ecommerce.orders.domain.events.OrderUpdateEvent;

public interface EventPublisherPort {
    
    public void publishOrderCreatedEvent(OrderCreatedEvent orderStatusEvent);

    public void publishOrderUpdateEvent(OrderUpdateEvent orderStatusEvent);

    public void publishOrderPaidEvent(OrderPaidEvent orderStatusEvent);

}
