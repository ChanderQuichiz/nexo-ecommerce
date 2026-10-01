package com.nexo.ecommerce.catalog.domain.value_objects;

public record Stock(Integer value) {

    public Stock {
        if (value == null) {
            throw new IllegalArgumentException(
                    "Stock cannot be null"
            );
        }

        if (value < 0) {
            throw new IllegalArgumentException(
                    "Stock cannot be negative"
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
                    "Quantity must be greater than zero"
            );
        }

        if (!isAvailable(quantity)) {
            throw new IllegalArgumentException(
                    "Insufficient stock"
            );
        }

        return new Stock(value - quantity);
    }
}