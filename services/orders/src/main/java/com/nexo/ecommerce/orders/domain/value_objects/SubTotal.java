package com.nexo.ecommerce.orders.domain.value_objects;

import java.math.BigDecimal;

public record SubTotal(BigDecimal value) {
    public SubTotal {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("SubTotal cannot be null or negative");
        }
        if (value.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("SubTotal cannot be zero");
        }
        if (value.scale() > 2) {
            throw new IllegalArgumentException("SubTotal cannot have more than 2 decimal places");
        }
    }
 
    
}
