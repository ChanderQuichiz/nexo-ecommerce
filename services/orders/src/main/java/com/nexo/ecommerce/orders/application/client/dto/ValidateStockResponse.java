package com.nexo.ecommerce.orders.application.client.dto;

import java.util.List;

public record ValidateStockResponse(
    Boolean available,
    List<ItemsValidateStock> items
) {
    
}

