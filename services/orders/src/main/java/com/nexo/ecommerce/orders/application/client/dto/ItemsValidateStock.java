package com.nexo.ecommerce.orders.application.client.dto;

public record ItemsValidateStock(
    String productId,
    Boolean hasStock,
    Integer remainingStock
) {
}

  