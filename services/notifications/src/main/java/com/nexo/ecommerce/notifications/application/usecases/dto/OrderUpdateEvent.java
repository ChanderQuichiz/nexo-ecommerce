package com.nexo.ecommerce.notifications.application.usecases.dto;

public record OrderUpdateEvent(
        String orderId,
        String newStatus,
        String customerId) {
}
