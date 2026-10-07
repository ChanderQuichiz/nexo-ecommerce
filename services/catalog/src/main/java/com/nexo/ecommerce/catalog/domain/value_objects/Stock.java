package com.nexo.ecommerce.catalog.domain.value_objects;

public record Stock(Integer value) {

    public Stock {
        if (value == null) {
            throw new IllegalArgumentException(
                    "El stock no puede ser nulo."
            );
        }

        if (value < 0) {
            throw new IllegalArgumentException(
                    "El stock no puede ser negativo."
            );
        }
    }

    public boolean isAvailable(Integer quantity) {
        return quantity != null
                && quantity > 0
                && value >= quantity;
    }

    public Stock reduce(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero."
            );
        }

        if (!isAvailable(quantity)) {
            throw new IllegalArgumentException(
                    "Stock insuficiente."
            );
        }

        return new Stock(value - quantity);
    }
}