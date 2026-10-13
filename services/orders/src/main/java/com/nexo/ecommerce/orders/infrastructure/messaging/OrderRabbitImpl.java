package com.nexo.ecommerce.orders.infrastructure.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.nexo.ecommerce.orders.application.ports.EventPublisherPort;
import com.nexo.ecommerce.orders.domain.events.OrderCreatedEvent;
import com.nexo.ecommerce.orders.domain.events.OrderPaidEvent;
import com.nexo.ecommerce.orders.domain.events.OrderUpdateEvent;

@Component
public class OrderRabbitImpl implements EventPublisherPort {
    private final RabbitTemplate rabbitTemplate;
    public OrderRabbitImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    @Override
    public void publishOrderCreatedEvent(OrderCreatedEvent orderStatusEvent) {
        rabbitTemplate.convertAndSend(OrderRabbitConfig.EXCHANGE, OrderRabbitConfig.ROUTING_KEY_CREATED, orderStatusEvent);
    }
    @Override
    public void publishOrderUpdateEvent(OrderUpdateEvent orderStatusEvent) {
        rabbitTemplate.convertAndSend(
                OrderRabbitConfig.EXCHANGE,
                OrderRabbitConfig.ROUTING_KEY_UPDATE,
                orderStatusEvent);
    }

    @Override
    public void publishOrderPaidEvent(OrderPaidEvent orderStatusEvent) {
        rabbitTemplate.convertAndSend(
                OrderRabbitConfig.EXCHANGE,
                OrderRabbitConfig.ROUTING_KEY_PAID,
                orderStatusEvent);
    }
}
