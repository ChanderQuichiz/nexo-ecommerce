package com.nexo.ecommerce.notifications.application.usecases.dto;

public record OrderPaidEvent(
        String orderId,
        String status,
        String customerId) {
}
