package com.nexo.ecommerce.orders.domain.value_objects;

import java.math.BigDecimal;

public record Item(BigDecimal price, String productId, Integer quantity) {
    public Item {
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be null or negative");
        }
       if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("ProductId cannot be null or blank");
        }
        if (productId.length() > 100) {
            throw new IllegalArgumentException("ProductId cannot be longer than 100 characters");
        }
        

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        if (price.scale() > 2) {
            throw new IllegalArgumentException("Price cannot have more than 2 decimal places");
        }
        
    }
    
    
}
