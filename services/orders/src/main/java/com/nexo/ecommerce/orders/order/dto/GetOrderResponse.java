package com.nexo.ecommerce.orders.order.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GetOrderResponse {
    private String id;
    private String userId;
    private BigDecimal subtotal;
    private BigDecimal shippingFee;
    private BigDecimal tax;
    private BigDecimal total;
    private String status;
    private LocalDateTime date;
    private List<OrderItem> items = new ArrayList<>();
    private String address;
    private String city;
    private String phone;

    // Constructor for JPA projection
    public GetOrderResponse(
        String id,
        String userId,
        BigDecimal subtotal,
        BigDecimal shippingFee,
        BigDecimal tax,
        BigDecimal total,
        String status,
        LocalDateTime date,
        List<OrderItem> items,
        String address,
        String city,
        String phone
    ) {
        this.id = id;
        this.userId = userId;
        this.subtotal = subtotal;
        this.shippingFee = shippingFee;
        this.tax = tax;
        this.total = total;
        this.status = status;
        this.date = date;
        this.items = items != null ? items : new ArrayList<>();
        this.address = address;
        this.city = city;
        this.phone = phone;
    }
}
