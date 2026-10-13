package com.nexo.ecommerce.orders.domain.events;

public record OrderUpdateEvent(
    String orderId,
    String newStatus,
    String customerId
) {
    
}
