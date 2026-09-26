package com.nexo.ecommerce.orders.usecases;

import static org.junit.jupiter.api.Assertions.*;
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

import com.nexo.ecommerce.orders.application.ports.PaymentPort;
import com.nexo.ecommerce.orders.application.repositories.OrderRepository;
import com.nexo.ecommerce.orders.application.usecases.CreateIntentPaymentUseCase;
import com.nexo.ecommerce.orders.application.usecases.dto.CreateIntentPaymentRequest;
import com.nexo.ecommerce.orders.application.usecases.dto.CreateIntentPaymentResponse;
import com.nexo.ecommerce.orders.domain.entities.Order;
import com.nexo.ecommerce.orders.domain.value_objects.Address;
import com.nexo.ecommerce.orders.domain.value_objects.City;
import com.nexo.ecommerce.orders.domain.value_objects.Item;
import com.nexo.ecommerce.orders.domain.value_objects.Phone;
import com.nexo.ecommerce.orders.domain.value_objects.UserId;

@ExtendWith(MockitoExtension.class)
class CreateIntentPaymentUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentPort paymentPort;

    @InjectMocks
    private CreateIntentPaymentUseCase createIntentPaymentUseCase;

    private Order sampleOrder;
    private String orderIdStr;

    @BeforeEach
    void setUp() {
        orderIdStr = UUID.randomUUID().toString();
        sampleOrder = Order.create(
            new UserId("user-123"),
            new Address("Av. Test 123"),
            new City("Lima"),
            new Phone("9876543210"),
            List.of(new Item(new BigDecimal("50.00"), UUID.randomUUID().toString(), 2))
        );
        try {
            java.lang.reflect.Field idField = Order.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(sampleOrder, new com.nexo.ecommerce.orders.domain.value_objects.OrderId(UUID.fromString(orderIdStr)));

            java.lang.reflect.Field piField = Order.class.getDeclaredField("paymentIntentId");
            piField.setAccessible(true);
            piField.set(sampleOrder, new java.util.ArrayList<>());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("CreateIntentPaymentUseCase debe crear intento de pago")
    void testCreateIntentPayment() {
        when(orderRepository.findById(orderIdStr)).thenReturn(sampleOrder);
        when(paymentPort.createPaymentIntent(eq(orderIdStr), any(BigDecimal.valueOf(107.00).getClass()), eq("USD"))).thenReturn("pi_123");
        
        CreateIntentPaymentResponse response = createIntentPaymentUseCase.execute(new CreateIntentPaymentRequest(orderIdStr));
        assertNotNull(response);
        assertEquals("pi_123", response.intentId());
    }
}
