package com.nexo.ecommerce.notifications.application.usecases.dto;

public record OrderCreatedEvent(
        String orderId,
        String customerId,
        String status) {
}
