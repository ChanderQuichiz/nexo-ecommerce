package com.nexo.ecommerce.orders.application.client.dto;

import java.math.BigDecimal;

public record GetProduct(
    String id,
    String name,
    String description,
    BigDecimal price,
    Integer stock,
    String imageUrl,
    String category,
    Boolean active

) {
    
}
