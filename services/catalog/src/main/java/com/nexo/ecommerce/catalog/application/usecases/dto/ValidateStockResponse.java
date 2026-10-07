package com.nexo.ecommerce.catalog.application.usecases.dto;

import java.util.List;

public record ValidateStockResponse(
        Boolean available,
        List<StockItemResponse> items
) {
}