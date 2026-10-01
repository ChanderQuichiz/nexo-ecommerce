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
    public static final String QUEUE = "orders.queue";
    public static final String ROUTING_KEY = "orders.created";

    @Bean
    public Queue ordersQueue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    public DirectExchange ordersExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Binding ordersBinding(
            Queue ordersQueue,
            DirectExchange ordersExchange
    ) {
        return BindingBuilder
                .bind(ordersQueue)
                .to(ordersExchange)
                .with(ROUTING_KEY);
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
