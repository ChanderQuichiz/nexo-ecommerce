package com.nexo.ecommerce.orders.domain.value_objects;

import java.math.BigDecimal;

public record ShippingFee(BigDecimal value) {
    public ShippingFee {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("ShippingFee cannot be null or negative");
        }
        if (value.scale() > 2) {
            throw new IllegalArgumentException("ShippingFee cannot have more than 2 decimal places");
        }
        
    }

    
}
