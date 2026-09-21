package com.nexo.ecommerce.orders.domain.value_objects;


public record UserId(String value) {
    public UserId {
       if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("UserId cannot be null or blank");
        }
        if (value.length() > 100) {
            throw new IllegalArgumentException("UserId cannot be longer than 100 characters");
        }
        
        
    }
    
    
}
