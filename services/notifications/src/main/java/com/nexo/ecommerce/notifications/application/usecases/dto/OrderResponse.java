package com.nexo.ecommerce.notifications.application.usecases.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        String id,
        String userId,
        String address,
        String phone,
        LocalDateTime date,
        String status,
        List<OrderItemResponse> items,
        BigDecimal subTotal,
        BigDecimal tax,
        BigDecimal total,
        List<String> paymentIntentId,
        BigDecimal shippingFee,
        String city) {
}
