package com.nexo.ecommerce.orders.domain.value_objects;

public record City(String value) {
    public City {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("City cannot be null or blank");
        }
        if (value.length() > 100) {
            throw new IllegalArgumentException("City cannot be longer than 100 characters");
        }
        
    }
    
}
