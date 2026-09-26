package com.nexo.ecommerce.orders.usecases;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nexo.ecommerce.orders.application.repositories.OrderRepository;
import com.nexo.ecommerce.orders.application.usecases.OrderReadByIdUseCase;
import com.nexo.ecommerce.orders.application.usecases.dto.OrderDto;
import com.nexo.ecommerce.orders.domain.entities.Order;
import com.nexo.ecommerce.orders.domain.value_objects.Address;
import com.nexo.ecommerce.orders.domain.value_objects.City;
import com.nexo.ecommerce.orders.domain.value_objects.Item;
import com.nexo.ecommerce.orders.domain.value_objects.Phone;
import com.nexo.ecommerce.orders.domain.value_objects.UserId;

@ExtendWith(MockitoExtension.class)
class OrderReadByIdUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderReadByIdUseCase orderReadByIdUseCase;

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
        // Forzar el id para que coincida con orderIdStr en la prueba
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

    @Nested
    @DisplayName("Pruebas para OrderReadByIdUseCase")
    class GetOrderByIdTests {

        @Test
        @DisplayName("Debe retornar el DTO de la orden cuando existe")
        void getOrderById_Success() {
            when(orderRepository.findById(orderIdStr)).thenReturn(sampleOrder);

            OrderDto response = orderReadByIdUseCase.execute(orderIdStr);

            assertNotNull(response);
            assertEquals("user-123", response.userId());
            assertEquals("Lima", response.city());
            verify(orderRepository, times(1)).findById(orderIdStr);
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando la orden no existe")
        void getOrderById_NotFound() {
            when(orderRepository.findById(orderIdStr)).thenReturn(null);

            assertThrows(Exception.class, () -> {
                orderReadByIdUseCase.execute(orderIdStr);
            });
            verify(orderRepository, times(1)).findById(orderIdStr);
        }
    }
}
