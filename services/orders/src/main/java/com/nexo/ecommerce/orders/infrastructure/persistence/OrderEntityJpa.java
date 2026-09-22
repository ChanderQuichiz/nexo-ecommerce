package com.nexo.ecommerce.orders.infrastructure.persistence;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter 
@Getter 
@Entity 
@Table(name = "orders")
public class OrderEntityJpa{
    @Id 
     
    private String id;

    private String userId;

    private BigDecimal subtotal;

    private BigDecimal shippingFee;

    private BigDecimal tax;

    private BigDecimal total;

    private String status;


    private LocalDateTime date;

    private String address;

    private String city;

    private String phone;

    @ElementCollection 
    @CollectionTable(name = "order_payment_intent_id")
    private List<String> paymentIntentId = new java.util.ArrayList<>();


    @ElementCollection 
    @CollectionTable(name = "order_items")
    private List<ItemEmbeddable> items = new java.util.ArrayList<>();
}
