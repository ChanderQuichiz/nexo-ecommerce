package com.nexo.ecommerce.orders.application.repositories;

import java.math.BigDecimal;
import java.util.List;

import com.nexo.ecommerce.orders.domain.entities.Order;

public interface OrderRepository {
    
    public void save(Order order);

    public Order findById(String orderId);

    public List<Order> findByUserId(String userId);

    public List<Order> findAll();

    public BigDecimal getOrderTotal(String orderId);

    public Order findByPaymentIntentId(String paymentIntentId);
}
