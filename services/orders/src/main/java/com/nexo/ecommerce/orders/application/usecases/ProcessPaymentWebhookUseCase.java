package com.nexo.ecommerce.orders.application.usecases;

import com.nexo.ecommerce.orders.application.ports.EventPublisherPort;
import com.nexo.ecommerce.orders.application.repositories.OrderRepository;
import com.nexo.ecommerce.orders.domain.entities.Order;
import com.nexo.ecommerce.orders.domain.events.OrderPaidEvent;
import com.nexo.ecommerce.orders.domain.value_objects.Status;

public class ProcessPaymentWebhookUseCase {

    private final OrderRepository orderRepository;
    private final EventPublisherPort eventPublisherPort;
    public ProcessPaymentWebhookUseCase(OrderRepository orderRepository, EventPublisherPort eventPublisherPort) {
        this.orderRepository = orderRepository;
        this.eventPublisherPort = eventPublisherPort;
    }

    public void execute(String paymentIntentId, String statusStr) {
        // Buscamos la orden que contenga este paymentIntentId
        Order order = orderRepository.findByPaymentIntentId(paymentIntentId);
        if (order != null) {
            Status newStatus = Status.valueOf(statusStr.toUpperCase());
            order.updateStatus(newStatus);
            orderRepository.save(order);
            OrderPaidEvent eventObject = new OrderPaidEvent(order.getId().value().toString(), newStatus.name(), order.getUserId().value());
            eventPublisherPort.publishOrderPaidEvent(eventObject);

        }
       if (statusStr.equalsIgnoreCase("failed")) {
            throw new RuntimeException("Payment failed for paymentIntentId: " + paymentIntentId);
        }
    }
}
