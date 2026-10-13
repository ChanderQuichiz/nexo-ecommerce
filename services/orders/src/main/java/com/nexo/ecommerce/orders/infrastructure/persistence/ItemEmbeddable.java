package com.nexo.ecommerce.orders.infrastructure.persistence;

import java.math.BigDecimal;

import jakarta.persistence.Embeddable;

@Embeddable 
public class ItemEmbeddable {
    private String productId;
    private Integer quantity;
    private BigDecimal price;
protected ItemEmbeddable() {
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
