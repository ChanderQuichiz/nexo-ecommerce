package com.nexo.ecommerce.orders.application.usecases;

import com.nexo.ecommerce.orders.application.repositories.OrderRepository;
import com.nexo.ecommerce.orders.domain.entities.Order;
import com.nexo.ecommerce.orders.domain.value_objects.Status;

public class ProcessPaymentWebhookUseCase {

    private final OrderRepository orderRepository;

    public ProcessPaymentWebhookUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void execute(String paymentIntentId, String statusStr) {
        // Buscamos la orden que contenga este paymentIntentId
        Order order = orderRepository.findByPaymentIntentId(paymentIntentId);
        if (order != null) {
            Status newStatus = Status.valueOf(statusStr.toUpperCase());
            order.updateStatus(newStatus);
            orderRepository.save(order);
        }
    }
}
