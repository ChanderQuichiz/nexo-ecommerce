package com.nexo.ecommerce.notifications.domain.value_objects;

import com.nexo.ecommerce.notifications.domain.exceptions.NotificationException;

import java.util.UUID;

public record CustomerId(UUID value) {

    public CustomerId {
        if (value == null) {
            throw new NotificationException("Customer ID must not be null");
        }
    }
}
