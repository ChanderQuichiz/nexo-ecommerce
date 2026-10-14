package com.nexo.ecommerce.notifications.usecases;

import com.nexo.ecommerce.notifications.application.ports.OrderQueryPort;
import com.nexo.ecommerce.notifications.application.ports.ReceiptGeneratorPort;
import com.nexo.ecommerce.notifications.application.ports.UserQueryPort;
import com.nexo.ecommerce.notifications.application.usecases.GenerateReceiptUseCase;
import com.nexo.ecommerce.notifications.application.usecases.dto.OrderResponse;
import com.nexo.ecommerce.notifications.application.usecases.dto.UserResponse;
import com.nexo.ecommerce.notifications.domain.exceptions.OrderNotPaidException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenerateReceiptUseCaseTest {

    @Mock
    private OrderQueryPort orderQueryPort;

    @Mock
    private UserQueryPort userQueryPort;

    @Mock
    private ReceiptGeneratorPort receiptGeneratorPort;

    private GenerateReceiptUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GenerateReceiptUseCase(orderQueryPort, userQueryPort, receiptGeneratorPort);
    }

    @ParameterizedTest
    @ValueSource(strings = {"PAID", "SHIPPED", "DELIVERED"})
    void shouldGenerateReceiptForEligibleStatus(String status) {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        OrderResponse order = order(orderId, customerId, status);
        UserResponse user = new UserResponse(customerId, "Ada", "ada@example.com");
        byte[] expectedPdf = {4, 5, 6};
        when(orderQueryPort.getOrderById(orderId.toString())).thenReturn(order);
        when(userQueryPort.getUserById(customerId)).thenReturn(user);
        when(receiptGeneratorPort.generateReceipt(order, user)).thenReturn(expectedPdf);

        byte[] result = useCase.execute(orderId.toString());

        assertArrayEquals(expectedPdf, result);
        verify(receiptGeneratorPort).generateReceipt(order, user);
    }

    @ParameterizedTest
    @ValueSource(strings = {"CREATED", "PENDING"})
    void shouldRejectUnpaidOrder(String status) {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        OrderResponse order = order(orderId, customerId, status);
        when(orderQueryPort.getOrderById(orderId.toString())).thenReturn(order);

        assertThrows(OrderNotPaidException.class, () -> useCase.execute(orderId.toString()));

        verifyNoInteractions(userQueryPort, receiptGeneratorPort);
    }

    private OrderResponse order(UUID orderId, UUID customerId, String status) {
        return new OrderResponse(
                orderId.toString(),
                customerId.toString(),
                "Main Street 1",
                "999999999",
                LocalDateTime.now(),
                status,
                List.of(),
                BigDecimal.TEN,
                BigDecimal.ONE,
                BigDecimal.valueOf(11),
                List.of("pi_123"),
                BigDecimal.ZERO,
                "Lima");
    }
}
