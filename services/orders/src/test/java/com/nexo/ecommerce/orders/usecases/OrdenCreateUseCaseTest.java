package com.nexo.ecommerce.orders.usecases;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nexo.ecommerce.orders.application.usecases.dto.OrdenCreateRequestDto;
import com.nexo.ecommerce.orders.application.usecases.dto.OrdenCreateResponseDto;
import com.nexo.ecommerce.orders.application.repositories.OrderRepository;
import com.nexo.ecommerce.orders.application.usecases.OrdenCreateUseCase;
import com.nexo.ecommerce.orders.domain.entities.Order;

import com.nexo.ecommerce.orders.application.client.CatalogClient;
import com.nexo.ecommerce.orders.application.client.dto.ValidateStockResponse;

@ExtendWith(MockitoExtension.class)
class OrdenCreateUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CatalogClient catalogClient;

    @InjectMocks
    private OrdenCreateUseCase ordenCreateUseCase;

    @Test
    @DisplayName("Debe crear la orden exitosamente")
    void createOrder_Success() {
        OrdenCreateRequestDto request = new OrdenCreateRequestDto(
            "user-123",
            "Av. Principal 123",
            "Lima",
            "+519876543210",
            List.of(new OrdenCreateRequestDto.Item(UUID.randomUUID().toString(), new BigDecimal("100.00"), 1))
        );

        when(catalogClient.validateStock(any())).thenReturn(new ValidateStockResponse(true, List.of()));

        OrdenCreateResponseDto response = ordenCreateUseCase.execute(request);

        assertNotNull(response);
        assertTrue(response.message().startsWith("Orden creada exitosamente"));
        verify(orderRepository, times(1)).save(any(Order.class));
    }
}
