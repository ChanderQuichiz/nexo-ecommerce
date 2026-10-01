package com.nexo.ecommerce.orders.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    static PostgreSQLContainer postgresContainer() {
        return new PostgreSQLContainer("postgres:15-alpine");
    }

@Bean
    @ServiceConnection
    static RabbitMQContainer rabbitMQContainer() {
        return new RabbitMQContainer("rabbitmq:3.12-management-alpine");
    }
}