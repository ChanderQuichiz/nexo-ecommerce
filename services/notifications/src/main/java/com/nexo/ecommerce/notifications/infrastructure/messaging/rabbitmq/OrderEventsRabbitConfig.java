package com.nexo.ecommerce.notifications.infrastructure.messaging.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderEventsRabbitConfig {

    @Bean
    public DirectExchange ordersExchange(
            @Value("${messaging.orders.exchange:orders.exchange}") String exchangeName) {
        return new DirectExchange(exchangeName, true, false);
    }

    @Bean
    public Queue notificationsQueue(
            @Value("${messaging.orders.queue:notifications.queue}") String queueName) {
        return new Queue(queueName, true);
    }

    @Bean
    public Binding ordersCreatedBinding(
            @Qualifier("notificationsQueue") Queue queue,
            @Qualifier("ordersExchange") DirectExchange exchange,
            @Value("${messaging.orders.routing-keys.created:orders.created}") String routingKey) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }

    @Bean
    public Binding ordersUpdatedBinding(
            @Qualifier("notificationsQueue") Queue queue,
            @Qualifier("ordersExchange") DirectExchange exchange,
            @Value("${messaging.orders.routing-keys.updated:orders.updated}") String routingKey) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }

    @Bean
    public Binding ordersPaidBinding(
            @Qualifier("notificationsQueue") Queue queue,
            @Qualifier("ordersExchange") DirectExchange exchange,
            @Value("${messaging.orders.routing-keys.paid:orders.paid}") String routingKey) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }
}
