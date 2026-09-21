package com.nexo.ecommerce.orders.application.usecases;

import com.nexo.ecommerce.orders.application.repositories.OrderRepository;
import com.nexo.ecommerce.orders.application.usecases.dto.UpdateStatusRequest;
import com.nexo.ecommerce.orders.application.usecases.dto.UpdateStatusResponse;
import com.nexo.ecommerce.orders.domain.entities.Order;
import com.nexo.ecommerce.orders.domain.value_objects.Status;

public class OrderUpdateStatusUseCase {

    private final OrderRepository orderRepository;

    public OrderUpdateStatusUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public UpdateStatusResponse execute(UpdateStatusRequest  request) {
        Order existingOrder = orderRepository.findById(request.orderId());
        if (existingOrder == null) {
            throw new IllegalArgumentException("Order not found with ID: " + request.orderId());
        }
        Status status = Status.valueOf(request.status().toUpperCase());
        existingOrder.updateStatus(status);
        orderRepository.save(existingOrder);

        return new UpdateStatusResponse("Order status updated successfully to: " + request.status());
    }
}