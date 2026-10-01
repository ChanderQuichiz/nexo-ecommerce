package com.nexo.ecommerce.catalog.domain.value_objects;

import java.math.BigDecimal;

public record Price(BigDecimal value) {

    public Price {
        if (value == null) {
            throw new IllegalArgumentException(
                    "Price cannot be null"
            );
        }

        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Price cannot be negative"
            );
        }

        if (value.scale() > 2) {
            throw new IllegalArgumentException(
                    "Price cannot have more than 2 decimal places"
            );
        }
    }
}