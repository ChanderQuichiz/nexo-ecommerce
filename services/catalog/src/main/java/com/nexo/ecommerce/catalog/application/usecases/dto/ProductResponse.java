package com.nexo.ecommerce.catalog.application.usecases.dto;

import com.nexo.ecommerce.catalog.domain.entities.Product;

import java.math.BigDecimal;

public record ProductResponse(
        String id,
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        String imageUrl,
        String category,
        Boolean active
) {

    public static ProductResponse fromDomain(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getImageUrl(),
                product.getCategory(),
                product.isActive()
        );
    }
}