package com.nexo.ecommerce.orders.application.client.dto;

import java.util.List;

import com.nexo.ecommerce.orders.domain.value_objects.Item;


public record ValidateStockRequest(
        List<Item> items
) {

}

