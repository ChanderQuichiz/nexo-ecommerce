package com.nexo.ecommerce.orders.catalog_client.dto;

import java.math.BigDecimal;

public record GetProduct(
    Long id,
    String name,
    String description,
    BigDecimal price,
    Integer stock,
    String imageUrl,
    String category,
    Boolean active

) {
    
}
