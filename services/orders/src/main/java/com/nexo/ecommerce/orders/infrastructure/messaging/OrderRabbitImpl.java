package com.nexo.ecommerce.orders.infrastructure.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.nexo.ecommerce.orders.application.ports.EventPublisherPort;
import com.nexo.ecommerce.orders.domain.events.OrderCreatedEvent;

@Component
public class OrderRabbitImpl implements EventPublisherPort {
    private final RabbitTemplate rabbitTemplate;
    public OrderRabbitImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    @Override
    public void publishOrderCreatedEvent(OrderCreatedEvent orderStatusEvent) {
        rabbitTemplate.convertAndSend(OrderRabbitConfig.EXCHANGE, OrderRabbitConfig.ROUTING_KEY, orderStatusEvent);
    }
}
