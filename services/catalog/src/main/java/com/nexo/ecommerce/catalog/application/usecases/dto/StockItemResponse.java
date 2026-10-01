package com.nexo.ecommerce.catalog.application.usecases.dto;

public record StockItemResponse(
        String productId,
        Boolean hasStock,
        Integer remainingStock
) {
}