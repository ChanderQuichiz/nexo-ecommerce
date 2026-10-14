package com.nexo.ecommerce.notifications.infrastructure.messaging.rabbitmq;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderEventsRabbitConfigTest {

    private static final String EXCHANGE = "orders.exchange";
    private static final String QUEUE = "notifications.queue";

    private OrderEventsRabbitConfig config;
    private DirectExchange exchange;
    private Queue queue;

    @BeforeEach
    void setUp() {
        config = new OrderEventsRabbitConfig();
        exchange = config.ordersExchange(EXCHANGE);
        queue = config.notificationsQueue(QUEUE);
    }

    @Test
    void shouldDeclareDurableDirectExchange() {
        assertInstanceOf(DirectExchange.class, exchange);
        assertEquals(EXCHANGE, exchange.getName());
        assertTrue(exchange.isDurable());
    }

    @Test
    void shouldDeclareDurableNotificationsQueue() {
        assertEquals(QUEUE, queue.getName());
        assertTrue(queue.isDurable());
    }

    @Test
    void shouldDeclareThreeBindingsWithExpectedRoutingKeys() {
        List<Binding> bindings = List.of(
                config.ordersCreatedBinding(queue, exchange, "orders.created"),
                config.ordersUpdatedBinding(queue, exchange, "orders.updated"),
                config.ordersPaidBinding(queue, exchange, "orders.paid"));

        assertEquals(3, bindings.size());
        assertEquals(
                List.of("orders.created", "orders.updated", "orders.paid"),
                bindings.stream().map(Binding::getRoutingKey).toList());
        bindings.forEach(binding -> {
            assertEquals(EXCHANGE, binding.getExchange());
            assertEquals(QUEUE, binding.getDestination());
        });
    }
}
