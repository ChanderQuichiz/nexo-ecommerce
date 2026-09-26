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

import com.nexo.ecommerce.orders.application.repositories.OrderRepository;
import com.nexo.ecommerce.orders.application.usecases.OrderUpdateStatusUseCase;
import com.nexo.ecommerce.orders.application.usecases.dto.UpdateStatusRequest;
import com.nexo.ecommerce.orders.application.usecases.dto.UpdateStatusResponse;
import com.nexo.ecommerce.orders.domain.entities.Order;
import com.nexo.ecommerce.orders.domain.value_objects.Address;
import com.nexo.ecommerce.orders.domain.value_objects.City;
import com.nexo.ecommerce.orders.domain.value_objects.Item;
import com.nexo.ecommerce.orders.domain.value_objects.Phone;
import com.nexo.ecommerce.orders.domain.value_objects.UserId;

@ExtendWith(MockitoExtension.class)
class OrderUpdateStatusUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderUpdateStatusUseCase orderUpdateStatusUseCase;

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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("OrderUpdateStatusUseCase debe actualizar estado correctamente")
    void testOrderUpdateStatus() {
        when(orderRepository.findById(orderIdStr)).thenReturn(sampleOrder);
        UpdateStatusResponse response = orderUpdateStatusUseCase.execute(new UpdateStatusRequest("PAID", orderIdStr));
        assertNotNull(response);
        verify(orderRepository).save(sampleOrder);
    }
}
