package com.nexo.ecommerce.catalog.application.usecases.dto;

public record StockItemRequest(
        String productId,
        Integer quantity
) {
}