package com.nexo.ecommerce.orders.order.dto;

import java.math.BigDecimal;

public record OrderItem(
    Long productId,
    String name,
    BigDecimal price,
    Integer quantity
) {
}
