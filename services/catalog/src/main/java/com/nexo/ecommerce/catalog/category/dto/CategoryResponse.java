package com.nexo.ecommerce.catalog.category.dto;

public record CategoryResponse(
        Long id,
        String name,
        String description,
        Boolean active
) {
}
