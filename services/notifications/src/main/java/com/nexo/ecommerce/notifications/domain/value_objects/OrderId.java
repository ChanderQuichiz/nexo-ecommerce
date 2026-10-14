package com.nexo.ecommerce.notifications.domain.value_objects;

import com.nexo.ecommerce.notifications.domain.exceptions.NotificationException;

import java.util.UUID;

public record OrderId(UUID value) {

    public OrderId {
        if (value == null) {
            throw new NotificationException("Order ID must not be null");
        }
    }
}
