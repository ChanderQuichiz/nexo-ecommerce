package com.nexo.ecommerce.orders.application.usecases;

import com.nexo.ecommerce.orders.application.repositories.OrderRepository;
import com.nexo.ecommerce.orders.application.usecases.dto.OrderDto;
import com.nexo.ecommerce.orders.application.usecases.dto.OrderMapperDto;
import com.nexo.ecommerce.orders.domain.entities.Order;

public class OrderReadByIdUseCase {
    private final OrderRepository orderRepository;
    public OrderReadByIdUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    public OrderDto execute(String orderId) {
        // Implementation for reading an order by ID
        Order order = orderRepository.findById(orderId);
        if(!order.getId().value().toString().equals(orderId)) {
            throw new RuntimeException("Order not found");
        }
        return OrderMapperDto.toDto(order);
    }
}
