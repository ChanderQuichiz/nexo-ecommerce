package com.nexo.ecommerce.orders.domain.value_objects;

import java.time.LocalDateTime;

public record Date(LocalDateTime value) {
    public Date {
        if (value == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        if (value.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Date cannot be in the future");
        }
        
    }
    
}
