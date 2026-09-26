package com.nexo.ecommerce.orders.domain.value_objects;

public record Address(String value) {
    public Address {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Address cannot be null or blank");
        }
        if (value.length() > 255) {
            throw new IllegalArgumentException("Address cannot be longer than 255 characters");
        }

    }
    
}
