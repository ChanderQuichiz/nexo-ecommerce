package com.nexo.ecommerce.orders.infrastructure.persistence;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;

import com.nexo.ecommerce.orders.application.repositories.OrderRepository;
import com.nexo.ecommerce.orders.domain.entities.Order;



@Repository
public class OrderRepositoryPgImpl implements OrderRepository {
    private final OrdenRepositoryJpa ordenRepositoryJpa;
    public OrderRepositoryPgImpl(OrdenRepositoryJpa ordenRepositoryJpa) {
        this.ordenRepositoryJpa = ordenRepositoryJpa;
    }

    @Override
    public void save(Order order) {
        // TODO Auto-generated method stub
        OrderEntityJpa orderEntityJpa = OrderMapper.toEntity(order);
        ordenRepositoryJpa.save(orderEntityJpa);
    }

    @Override
    public Order findById(String orderId) {
        // TODO Auto-generated method stub
        OrderEntityJpa orderEntityJpa = ordenRepositoryJpa.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        Order order = OrderMapper.toDomain(orderEntityJpa);
        return order;

    }
    // Implementación específica para PostgreSQL

    @Override
    public List<Order> findByUserId(String userId) {
        // TODO Auto-generated method stub
        List<OrderEntityJpa> orderEntities = ordenRepositoryJpa.findByUserId(userId);
        return orderEntities.stream()
                .map(OrderMapper::toDomain)
                .toList();
    }

    @Override
    public List<Order> findAll() {
        // TODO Auto-generated method stub
        List<OrderEntityJpa> orderEntities = ordenRepositoryJpa.findAll();
        return orderEntities.stream()
                .map(OrderMapper::toDomain)
                .toList();
    }

    @Override
    public BigDecimal getOrderTotal(String orderId) {
        // TODO Auto-generated method stub
        return ordenRepositoryJpa.getOrderTotal(orderId);
    }

    @Override
    public Order findByPaymentIntentId(String paymentIntentId) {
        return ordenRepositoryJpa.findByPaymentIntentId(paymentIntentId)
                .map(OrderMapper::toDomain)
                .orElse(null);
    }


  
}
