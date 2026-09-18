package com.nexo.ecommerce.orders.catalog_client.dto;

import java.util.List;

import com.nexo.ecommerce.orders.order.dto.CreateOrderItem;

public record ValidateStockRequest(
        List<CreateOrderItem> items
) {

}

