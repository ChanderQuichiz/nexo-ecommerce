package com.nexo.ecommerce.notifications.infrastructure.messaging.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexo.ecommerce.notifications.application.usecases.HandleOrderCreatedUseCase;
import com.nexo.ecommerce.notifications.application.usecases.HandleOrderPaidUseCase;
import com.nexo.ecommerce.notifications.application.usecases.HandleOrderUpdatedUseCase;
import com.nexo.ecommerce.notifications.application.usecases.dto.OrderCreatedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers(disabledWithoutDocker = true)
class OrderEventsRabbitIT {

    @Container
    static final RabbitMQContainer RABBITMQ = new RabbitMQContainer(
            DockerImageName.parse("rabbitmq:3.13-management-alpine"));

    @DynamicPropertySource
    static void rabbitProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", RABBITMQ::getHost);
        registry.add("spring.rabbitmq.port", RABBITMQ::getAmqpPort);
        registry.add("spring.rabbitmq.username", RABBITMQ::getAdminUsername);
        registry.add("spring.rabbitmq.password", RABBITMQ::getAdminPassword);
        registry.add("spring.rabbitmq.listener.simple.auto-startup", () -> true);
        registry.add("eureka.client.enabled", () -> false);
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private HandleOrderCreatedUseCase handleOrderCreatedUseCase;

    @MockBean
    private HandleOrderUpdatedUseCase handleOrderUpdatedUseCase;

    @MockBean
    private HandleOrderPaidUseCase handleOrderPaidUseCase;

    @Test
    void shouldConsumeOrderCreatedEventAndDelegateToUseCase() throws Exception {
        String orderId = UUID.randomUUID().toString();
        String customerId = UUID.randomUUID().toString();
        OrderCreatedEvent event = new OrderCreatedEvent(orderId, customerId, "PENDING");

        rabbitTemplate.convertAndSend(
                "orders.exchange",
                "orders.created",
                objectMapper.writeValueAsBytes(event));

        verify(handleOrderCreatedUseCase, timeout(10_000)).execute(argThat(received ->
                orderId.equals(received.orderId())
                        && customerId.equals(received.customerId())
                        && "PENDING".equals(received.status())));
    }
}
