package com.nexo.ecommerce.orders.domain.value_objects;

public record Phone(String value) {
    public Phone {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Phone cannot be null or blank");
        }
        if (!value.matches("\\+?\\d{10,15}")) {
            throw new IllegalArgumentException("Phone must be a valid phone number with 10 to 15 digits, optionally starting with '+'");
        }
     
        
    }
    
}
