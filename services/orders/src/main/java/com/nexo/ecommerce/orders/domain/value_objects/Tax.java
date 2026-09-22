package com.nexo.ecommerce.orders.domain.value_objects;

import java.math.BigDecimal;

public record Tax(BigDecimal value) {
    public Tax {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Tax cannot be null or negative");
        }
        if (value.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("Tax cannot be greater than 100%");
        }
        if (value.scale() > 2) {
            throw new IllegalArgumentException("Tax cannot have more than 2 decimal places");
        }
        
    }

    
}
