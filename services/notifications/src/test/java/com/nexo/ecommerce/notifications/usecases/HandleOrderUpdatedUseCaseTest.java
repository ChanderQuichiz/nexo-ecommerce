package com.nexo.ecommerce.notifications.usecases;

import com.nexo.ecommerce.notifications.application.ports.EmailSenderPort;
import com.nexo.ecommerce.notifications.application.ports.UserQueryPort;
import com.nexo.ecommerce.notifications.application.usecases.HandleOrderUpdatedUseCase;
import com.nexo.ecommerce.notifications.application.usecases.dto.OrderUpdateEvent;
import com.nexo.ecommerce.notifications.application.usecases.dto.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HandleOrderUpdatedUseCaseTest {

    @Mock
    private UserQueryPort userQueryPort;

    @Mock
    private EmailSenderPort emailSenderPort;

    private HandleOrderUpdatedUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new HandleOrderUpdatedUseCase(userQueryPort, emailSenderPort);
    }

    @ParameterizedTest
    @CsvSource({
            "SHIPPED,enviada",
            "DELIVERED,entregada",
            "CANCELED,cancelada"
    })
    void shouldSendEmailForNotifiableStatus(String status, String expectedMessage) {
        UUID customerId = UUID.randomUUID();
        OrderUpdateEvent event = new OrderUpdateEvent(
                UUID.randomUUID().toString(),
                status,
                customerId.toString());
        when(userQueryPort.getUserById(customerId))
                .thenReturn(new UserResponse(customerId, "Ada", "ada@example.com"));

        useCase.execute(event);

        ArgumentCaptor<String> body = ArgumentCaptor.forClass(String.class);
        verify(emailSenderPort).sendSimpleEmail(any(), any(), body.capture());
        assertTrue(body.getValue().contains(expectedMessage));
    }

    @Test
    void shouldIgnorePendingWithoutQueryingUser() {
        useCase.execute(ignoredEvent("PENDING"));

        verifyNoInteractions(userQueryPort);
        verify(emailSenderPort, never()).sendSimpleEmail(any(), any(), any());
    }

    @Test
    void shouldIgnorePaidWithoutQueryingUser() {
        useCase.execute(ignoredEvent("PAID"));

        verifyNoInteractions(userQueryPort);
        verify(emailSenderPort, never()).sendSimpleEmail(any(), any(), any());
    }

    private OrderUpdateEvent ignoredEvent(String status) {
        return new OrderUpdateEvent("not-evaluated", status, "not-evaluated");
    }
}
