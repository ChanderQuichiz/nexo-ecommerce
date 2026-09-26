package com.nexo.ecommerce.orders.usecases;

import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nexo.ecommerce.orders.application.repositories.OrderRepository;
import com.nexo.ecommerce.orders.application.usecases.ProcessPaymentWebhookUseCase;
import com.nexo.ecommerce.orders.domain.entities.Order;
import com.nexo.ecommerce.orders.domain.value_objects.Address;
import com.nexo.ecommerce.orders.domain.value_objects.City;
import com.nexo.ecommerce.orders.domain.value_objects.Item;
import com.nexo.ecommerce.orders.domain.value_objects.Phone;
import com.nexo.ecommerce.orders.domain.value_objects.UserId;

@ExtendWith(MockitoExtension.class)
class ProcessPaymentWebhookUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private ProcessPaymentWebhookUseCase processPaymentWebhookUseCase;

    private Order sampleOrder;

    @BeforeEach
    void setUp() {
        sampleOrder = Order.create(
            new UserId("user-123"),
            new Address("Av. Test 123"),
            new City("Lima"),
            new Phone("9876543210"),
            List.of(new Item(new BigDecimal("50.00"), UUID.randomUUID().toString(), 2))
        );
        try {
            java.lang.reflect.Field piField = Order.class.getDeclaredField("paymentIntentId");
            piField.setAccessible(true);
            piField.set(sampleOrder, new java.util.ArrayList<>());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("ProcessPaymentWebhookUseCase debe procesar webhook de pago")
    void testProcessPaymentWebhook() {
        when(orderRepository.findByPaymentIntentId("pi_123")).thenReturn(sampleOrder);
        processPaymentWebhookUseCase.execute("pi_123", "PAID");
        verify(orderRepository).save(sampleOrder);
    }
}
