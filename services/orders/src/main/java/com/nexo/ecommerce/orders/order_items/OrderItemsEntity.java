package com.nexo.ecommerce.orders.order_items;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Table(name = "order_items")
@Entity 
@Getter 
@Setter 
public class OrderItemsEntity {
    @Id 
    @GeneratedValue(generator = "uuid2")
    private String id;

    private String orderId;
    
    private Long productId;
    private String name;
    private BigDecimal price;
    private Integer quantity;
}
