package com.nexo.ecommerce.orders.infrastructure.persistence;

import java.math.BigDecimal;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Setter 
@Getter 
@Embeddable 
public class ItemEmbeddable {
    private String productId;
    private Integer quantity;
    private BigDecimal price;
protected ItemEmbeddable() {
    }

 
}
