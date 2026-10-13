package com.nexo.ecommerce.orders.application.usecases;

import com.nexo.ecommerce.orders.application.ports.EventPublisherPort;
import com.nexo.ecommerce.orders.application.repositories.OrderRepository;
import com.nexo.ecommerce.orders.application.usecases.dto.UpdateStatusRequest;
import com.nexo.ecommerce.orders.application.usecases.dto.UpdateStatusResponse;
import com.nexo.ecommerce.orders.domain.entities.Order;
import com.nexo.ecommerce.orders.domain.events.OrderUpdateEvent;
import com.nexo.ecommerce.orders.domain.value_objects.Status;

public class OrderUpdateStatusUseCase {

    private final OrderRepository orderRepository;
    private final EventPublisherPort eventPublisherPort;
    public OrderUpdateStatusUseCase(OrderRepository orderRepository, EventPublisherPort eventPublisherPort) {
        this.orderRepository = orderRepository;
        this.eventPublisherPort = eventPublisherPort;
    }

    public UpdateStatusResponse execute(UpdateStatusRequest  request) {
        Order existingOrder = orderRepository.findById(request.orderId());
        if (existingOrder == null) {
            throw new IllegalArgumentException("Order not found with ID: " + request.orderId());
        }
        Status status = Status.valueOf(request.status().toUpperCase());
        existingOrder.updateStatus(status);
        orderRepository.save(existingOrder);

        OrderUpdateEvent eventObject = new OrderUpdateEvent(existingOrder.getId().value().toString(), existingOrder.getStatus().name(), existingOrder.getUserId().value());

        eventPublisherPort.publishOrderUpdateEvent(eventObject);


        return new UpdateStatusResponse("Order status updated successfully to: " + request.status());
    }
}