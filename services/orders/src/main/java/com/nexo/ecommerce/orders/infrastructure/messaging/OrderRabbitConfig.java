package com.nexo.ecommerce.orders.infrastructure.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderRabbitConfig {
    

    public static final String EXCHANGE = "orders.exchange";
    public static final String QUEUE = "notifications.queue";
    public static final String ROUTING_KEY_CREATED = "orders.created";
    public static final String ROUTING_KEY_UPDATE = "orders.updated";
    public static final String ROUTING_KEY_PAID = "orders.paid";


    @Bean
    public Queue notificationsQueue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    public DirectExchange ordersExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Binding ordersCreatedNotificationBinding(
            Queue notificationsQueue,
            DirectExchange ordersExchange
    ) {
        return BindingBuilder
                .bind(notificationsQueue)
                .to(ordersExchange)
                .with(ROUTING_KEY_CREATED);
    }

    @Bean
    public Binding ordersUpdateNotificationBinding(
            Queue notificationsQueue,
            DirectExchange ordersExchange
    ) {
        return BindingBuilder
                .bind(notificationsQueue)
                .to(ordersExchange)
                .with(ROUTING_KEY_UPDATE);
    }

    @Bean
    public Binding ordersPaidNotificationBinding(
            Queue notificationsQueue,
            DirectExchange ordersExchange
    ) {
        return BindingBuilder
                .bind(notificationsQueue)
                .to(ordersExchange)
                .with(ROUTING_KEY_PAID);
    }


    @Bean
    public Jackson2JsonMessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }


    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter
    ) {
        RabbitTemplate template =
                new RabbitTemplate(connectionFactory);

        template.setMessageConverter(converter);

        return template;
    }

}
