package com.nexo.ecommerce.notifications.infrastructure.messaging.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexo.ecommerce.notifications.application.usecases.HandleOrderCreatedUseCase;
import com.nexo.ecommerce.notifications.application.usecases.HandleOrderPaidUseCase;
import com.nexo.ecommerce.notifications.application.usecases.HandleOrderUpdatedUseCase;
import com.nexo.ecommerce.notifications.application.usecases.dto.OrderCreatedEvent;
import com.nexo.ecommerce.notifications.application.usecases.dto.OrderPaidEvent;
import com.nexo.ecommerce.notifications.application.usecases.dto.OrderUpdateEvent;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OrderEventsListener {

    private final ObjectMapper objectMapper;
    private final HandleOrderCreatedUseCase handleOrderCreatedUseCase;
    private final HandleOrderUpdatedUseCase handleOrderUpdatedUseCase;
    private final HandleOrderPaidUseCase handleOrderPaidUseCase;
    private final String createdRoutingKey;
    private final String updatedRoutingKey;
    private final String paidRoutingKey;

    public OrderEventsListener(
            ObjectMapper objectMapper,
            HandleOrderCreatedUseCase handleOrderCreatedUseCase,
            HandleOrderUpdatedUseCase handleOrderUpdatedUseCase,
            HandleOrderPaidUseCase handleOrderPaidUseCase,
            @Value("${messaging.orders.routing-keys.created:orders.created}") String createdRoutingKey,
            @Value("${messaging.orders.routing-keys.updated:orders.updated}") String updatedRoutingKey,
            @Value("${messaging.orders.routing-keys.paid:orders.paid}") String paidRoutingKey) {
        this.objectMapper = objectMapper;
        this.handleOrderCreatedUseCase = handleOrderCreatedUseCase;
        this.handleOrderUpdatedUseCase = handleOrderUpdatedUseCase;
        this.handleOrderPaidUseCase = handleOrderPaidUseCase;
        this.createdRoutingKey = createdRoutingKey;
        this.updatedRoutingKey = updatedRoutingKey;
        this.paidRoutingKey = paidRoutingKey;
    }

    @RabbitListener(queues = "${messaging.orders.queue:notifications.queue}")
    public void consume(Message message) throws IOException {
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();

        if (createdRoutingKey.equals(routingKey)) {
            OrderCreatedEvent event = objectMapper.readValue(message.getBody(), OrderCreatedEvent.class);
            handleOrderCreatedUseCase.execute(event);
            return;
        }
        if (updatedRoutingKey.equals(routingKey)) {
            OrderUpdateEvent event = objectMapper.readValue(message.getBody(), OrderUpdateEvent.class);
            handleOrderUpdatedUseCase.execute(event);
            return;
        }
        if (paidRoutingKey.equals(routingKey)) {
            OrderPaidEvent event = objectMapper.readValue(message.getBody(), OrderPaidEvent.class);
            handleOrderPaidUseCase.execute(event);
            return;
        }

        throw new UnsupportedRoutingKeyException(routingKey);
    }
}
