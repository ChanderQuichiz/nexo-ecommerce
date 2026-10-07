package com.nexo.ecommerce.catalog.domain.value_objects;

import java.util.UUID;

public record ProductId(String value) {

    public ProductId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "ProductId no puede ser nulo o en blanco"
            );
        }
    }

    public static ProductId generate() {
        return new ProductId(UUID.randomUUID().toString());
    }
}