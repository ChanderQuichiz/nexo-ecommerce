package com.nexo.ecommerce.catalog.application.usecases.dto;

import java.math.BigDecimal;

public record CreateProductRequest(
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        String category
) {
}