package com.nexo.ecommerce.catalog.domain.value_objects;

import java.math.BigDecimal;

public record Price(BigDecimal value) {

    public Price {
        if (value == null) {
            throw new IllegalArgumentException(
                    "El precio no puede ser nulo."
            );
        }

        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "El precio no puede ser negativo."
            );
        }

        if (value.scale() > 2) {
            throw new IllegalArgumentException(
                    "El precio no puede tener más de 2 decimales."
            );
        }
    }
}