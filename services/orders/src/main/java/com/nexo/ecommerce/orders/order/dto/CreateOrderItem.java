package com.nexo.ecommerce.orders.order.dto;

public record CreateOrderItem(
    Long productId,
    Integer quantity
) {
    
}