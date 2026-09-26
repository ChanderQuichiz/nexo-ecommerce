package com.nexo.ecommerce.catalog.category.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateCategoryStatusRequest(
        @NotNull(message = "Category status is required")
        Boolean active
) {
}
