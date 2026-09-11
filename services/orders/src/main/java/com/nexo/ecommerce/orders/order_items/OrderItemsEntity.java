package com.nexo.ecommerce.orders.order_items;

import java.math.BigDecimal;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table(name = "order_items")
@Entity 
@Getter 
@Setter 
public class OrderItemsEntity {
    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "order_id", nullable = false)
    private String orderId;
    
    @Column(name = "product_id")
    private Long productId;

    private String name;
    private BigDecimal price;
    private Integer quantity;
}
