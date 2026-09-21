package com.nexo.ecommerce.orders.domain.value_objects;

public record PaymentIntentId(String value) {
    public PaymentIntentId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("PaymentIntentId cannot be null or blank");
        }
        if (value.length() > 255) {
            throw new IllegalArgumentException("PaymentIntentId cannot be longer than 255 characters");
        }
        
    }
    
}
