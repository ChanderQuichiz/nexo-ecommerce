package com.nexo.ecommerce.orders.order.dto;

import java.util.List;

public record CreateOrderRequest(
   List<CreateOrderItem> items,
   String address,
   String city,
   String phone
) {
    
}

