package com.nexo.ecommerce.notifications.infrastructure.config;

import com.nexo.ecommerce.notifications.application.ports.EmailSenderPort;
import com.nexo.ecommerce.notifications.application.ports.OrderQueryPort;
import com.nexo.ecommerce.notifications.application.ports.ReceiptGeneratorPort;
import com.nexo.ecommerce.notifications.application.ports.UserQueryPort;
import com.nexo.ecommerce.notifications.application.usecases.GenerateReceiptUseCase;
import com.nexo.ecommerce.notifications.application.usecases.HandleOrderCreatedUseCase;
import com.nexo.ecommerce.notifications.application.usecases.HandleOrderPaidUseCase;
import com.nexo.ecommerce.notifications.application.usecases.HandleOrderUpdatedUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCasesConfig {

    @Bean
    public HandleOrderCreatedUseCase handleOrderCreatedUseCase(
            UserQueryPort userQueryPort,
            EmailSenderPort emailSenderPort) {
        return new HandleOrderCreatedUseCase(userQueryPort, emailSenderPort);
    }

    @Bean
    public HandleOrderUpdatedUseCase handleOrderUpdatedUseCase(
            UserQueryPort userQueryPort,
            EmailSenderPort emailSenderPort) {
        return new HandleOrderUpdatedUseCase(userQueryPort, emailSenderPort);
    }

    @Bean
    public HandleOrderPaidUseCase handleOrderPaidUseCase(
            OrderQueryPort orderQueryPort,
            UserQueryPort userQueryPort,
            ReceiptGeneratorPort receiptGeneratorPort,
            EmailSenderPort emailSenderPort) {
        return new HandleOrderPaidUseCase(
                orderQueryPort,
                userQueryPort,
                receiptGeneratorPort,
                emailSenderPort);
    }

    @Bean
    public GenerateReceiptUseCase generateReceiptUseCase(
            OrderQueryPort orderQueryPort,
            UserQueryPort userQueryPort,
            ReceiptGeneratorPort receiptGeneratorPort) {
        return new GenerateReceiptUseCase(orderQueryPort, userQueryPort, receiptGeneratorPort);
    }
}
