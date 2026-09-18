package com.nexo.ecommerce.orders.catalog_client.dto;

public record ItemsValidateStock(
    Long productId,
    Boolean hasStock,
    Integer remainingStock
) {
}

  