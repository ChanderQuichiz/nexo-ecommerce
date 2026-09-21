package com.nexo.ecommerce.orders.application.usecases.dto;

import java.math.BigDecimal;


public record OrderDto(
    String id,
    String userId,
    String address,
    String phone,
    String date,
    String status,
    java.util.List<ItemDto> items,
    BigDecimal subTotal,
    BigDecimal tax,
    BigDecimal total,
    java.util.List<String> paymentIntentId,
    BigDecimal shippingFee,
    String city


) {
    
    public record ItemDto(
        String productId,
        Integer quantity,
        BigDecimal price
    ) {}


}
