package com.nexo.ecommerce.orders.catalog_client.dto;

import java.util.List;

public record ValidateStockResponse(
    Boolean available,
    List<ItemsValidateStock> items
) {
    
}

