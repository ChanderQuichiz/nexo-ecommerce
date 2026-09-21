package com.nexo.ecommerce.orders.domain.value_objects;

import java.util.UUID;

public record OrderId(UUID value) {

    public OrderId {
        if (value == null) {
            throw new IllegalArgumentException("OrderId cannot be null");
        }
        
    }
    public OrderId(String value) {
        this(UUID.fromString(value));
    }
 
}
