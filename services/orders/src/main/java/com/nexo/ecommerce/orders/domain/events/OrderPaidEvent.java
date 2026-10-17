package com.nexo.ecommerce.orders.domain.events;

public record OrderPaidEvent(
    String orderId, 
    String status, 
    String customerId) {
    
}
