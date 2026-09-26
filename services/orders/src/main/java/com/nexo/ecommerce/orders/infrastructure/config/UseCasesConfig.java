package com.nexo.ecommerce.orders.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nexo.ecommerce.orders.application.client.CatalogClient;
import com.nexo.ecommerce.orders.application.ports.EventPublisherPort;
import com.nexo.ecommerce.orders.application.ports.PaymentPort;
import com.nexo.ecommerce.orders.application.repositories.OrderRepository;
import com.nexo.ecommerce.orders.application.usecases.CreateIntentPaymentUseCase;
import com.nexo.ecommerce.orders.application.usecases.OrdenCreateUseCase;
import com.nexo.ecommerce.orders.application.usecases.OrderReadAllUseCase;
import com.nexo.ecommerce.orders.application.usecases.OrderReadByIdUseCase;
import com.nexo.ecommerce.orders.application.usecases.OrderReadMeUseCase;
import com.nexo.ecommerce.orders.application.usecases.OrderUpdateStatusUseCase;
import com.nexo.ecommerce.orders.application.usecases.ProcessPaymentWebhookUseCase;

@Configuration
public class UseCasesConfig {

    @Bean
    public OrdenCreateUseCase ordenCreateUseCase(OrderRepository orderRepository, CatalogClient catalogClient, EventPublisherPort eventPublisherPort) {
        return new OrdenCreateUseCase(orderRepository, catalogClient, eventPublisherPort);
    }

    @Bean
    public OrderReadAllUseCase orderReadAllUseCase(OrderRepository orderRepository) {
        return new OrderReadAllUseCase(orderRepository);
    }

    @Bean
    public OrderReadByIdUseCase orderReadByIdUseCase(OrderRepository orderRepository) {
        return new OrderReadByIdUseCase(orderRepository);
    }

    @Bean
    public OrderReadMeUseCase orderReadMeUseCase(OrderRepository orderRepository) {
        return new OrderReadMeUseCase(orderRepository);
    }

    @Bean
    public OrderUpdateStatusUseCase orderUpdateStatusUseCase(OrderRepository orderRepository) {
        return new OrderUpdateStatusUseCase(orderRepository);
    }

    @Bean
    public ProcessPaymentWebhookUseCase processPaymentWebhookUseCase(OrderRepository orderRepository) {
        return new ProcessPaymentWebhookUseCase(orderRepository);
    }

    @Bean
    public CreateIntentPaymentUseCase createIntentPaymentUseCase(OrderRepository orderRepository, PaymentPort paymentPort) {
        return new CreateIntentPaymentUseCase(orderRepository, paymentPort);
    }
}
