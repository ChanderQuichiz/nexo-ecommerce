package com.nexo.ecommerce.notifications.application.usecases.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
        String productId,
        Integer quantity,
        BigDecimal price) {
}
