package com.nexo.ecommerce.catalog.application.usecases.dto;

import java.util.List;

public record ValidateStockRequest(
        List<StockItemRequest> items
) {
}