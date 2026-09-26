package com.nexo.ecommerce.orders.domain.events;

public record OrderCreatedEvent(
    String orderId
) {
    
}
