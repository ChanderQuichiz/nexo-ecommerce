package com.nexo.ecommerce.orders.order;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class OrderEntity {
    @Id 
    @GeneratedValue(generator = "uuid2")
    private String id;

    @Column(name = "user_id")
    private String userId;

    private BigDecimal subtotal;

    @Column(name = "shipping_fee")
    private BigDecimal shippingFee;

    private BigDecimal tax;
    private BigDecimal total;

    private String status;

    @CreationTimestamp 
    private LocalDateTime date;

    private String address;
    private String city;
    private String phone;

    @Column(name = "stripe_payment_intent_id")
    private String stripePaymentIntentId;
}



