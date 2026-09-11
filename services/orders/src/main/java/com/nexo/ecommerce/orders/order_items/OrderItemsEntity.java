package com.nexo.ecommerce.orders.order_items;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Table(name = "order_items")
@Entity 
@Getter 
@Setter 
public class OrderItemsEntity {
    @Id 
    @GeneratedValue
    private String id;
    @ManyToOne
    @JoinColumn(name = "orderId", referencedColumnName = "id")
    private String orderId;
    private Long productId;
    private Integer quantity;
}
