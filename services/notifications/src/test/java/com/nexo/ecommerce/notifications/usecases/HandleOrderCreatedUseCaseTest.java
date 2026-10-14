package com.nexo.ecommerce.notifications.usecases;

import com.nexo.ecommerce.notifications.application.ports.EmailSenderPort;
import com.nexo.ecommerce.notifications.application.ports.ReceiptGeneratorPort;
import com.nexo.ecommerce.notifications.application.ports.UserQueryPort;
import com.nexo.ecommerce.notifications.application.usecases.HandleOrderCreatedUseCase;
import com.nexo.ecommerce.notifications.application.usecases.dto.OrderCreatedEvent;
import com.nexo.ecommerce.notifications.application.usecases.dto.UserResponse;
import com.nexo.ecommerce.notifications.domain.exceptions.NotificationException;
import com.nexo.ecommerce.notifications.domain.value_objects.EmailAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HandleOrderCreatedUseCaseTest {

    @Mock
    private UserQueryPort userQueryPort;

    @Mock
    private EmailSenderPort emailSenderPort;

    @Mock
    private ReceiptGeneratorPort receiptGeneratorPort;

    private HandleOrderCreatedUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new HandleOrderCreatedUseCase(userQueryPort, emailSenderPort);
    }

    @Test
    void shouldSendSimpleEmailForValidEventWithoutGeneratingReceipt() {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UserResponse user = new UserResponse(customerId, "Ada", "ada@example.com");
        when(userQueryPort.getUserById(customerId)).thenReturn(user);

        useCase.execute(new OrderCreatedEvent(orderId.toString(), customerId.toString(), "CREATED"));

        ArgumentCaptor<EmailAddress> recipient = ArgumentCaptor.forClass(EmailAddress.class);
        ArgumentCaptor<String> subject = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> body = ArgumentCaptor.forClass(String.class);
        verify(emailSenderPort).sendSimpleEmail(recipient.capture(), subject.capture(), body.capture());
        assertEquals("ada@example.com", recipient.getValue().value());
        assertTrue(subject.getValue().contains("creada"));
        assertTrue(body.getValue().contains(orderId.toString()));
        verifyNoInteractions(receiptGeneratorPort);
    }

    @Test
    void shouldRejectNullEvent() {
        assertThrows(NotificationException.class, () -> useCase.execute(null));
        verifyNoInteractions(userQueryPort, emailSenderPort, receiptGeneratorPort);
    }
}
