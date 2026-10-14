package com.nexo.ecommerce.notifications.usecases;

import com.nexo.ecommerce.notifications.application.ports.EmailSenderPort;
import com.nexo.ecommerce.notifications.application.ports.OrderQueryPort;
import com.nexo.ecommerce.notifications.application.ports.ReceiptGeneratorPort;
import com.nexo.ecommerce.notifications.application.ports.UserQueryPort;
import com.nexo.ecommerce.notifications.application.usecases.HandleOrderPaidUseCase;
import com.nexo.ecommerce.notifications.application.usecases.dto.EmailAttachment;
import com.nexo.ecommerce.notifications.application.usecases.dto.OrderPaidEvent;
import com.nexo.ecommerce.notifications.application.usecases.dto.OrderResponse;
import com.nexo.ecommerce.notifications.application.usecases.dto.UserResponse;
import com.nexo.ecommerce.notifications.domain.exceptions.NotificationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HandleOrderPaidUseCaseTest {

    @Mock
    private OrderQueryPort orderQueryPort;

    @Mock
    private UserQueryPort userQueryPort;

    @Mock
    private ReceiptGeneratorPort receiptGeneratorPort;

    @Mock
    private EmailSenderPort emailSenderPort;

    private HandleOrderPaidUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new HandleOrderPaidUseCase(
                orderQueryPort,
                userQueryPort,
                receiptGeneratorPort,
                emailSenderPort);
    }

    @Test
    void shouldQueryDataGenerateReceiptAndSendPdfAttachment() {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        OrderPaidEvent event = new OrderPaidEvent(orderId.toString(), "PAID", customerId.toString());
        OrderResponse order = order(orderId, customerId, "PAID");
        UserResponse user = new UserResponse(customerId, "Ada", "ada@example.com");
        byte[] pdf = {1, 2, 3};
        when(orderQueryPort.getOrderById(orderId.toString())).thenReturn(order);
        when(userQueryPort.getUserById(customerId)).thenReturn(user);
        when(receiptGeneratorPort.generateReceipt(order, user)).thenReturn(pdf);

        useCase.execute(event);

        verify(orderQueryPort).getOrderById(orderId.toString());
        verify(userQueryPort).getUserById(customerId);
        verify(receiptGeneratorPort).generateReceipt(order, user);
        ArgumentCaptor<EmailAttachment> attachment = ArgumentCaptor.forClass(EmailAttachment.class);
        verify(emailSenderPort).sendEmailWithAttachment(any(), any(), any(), attachment.capture());
        assertEquals("receipt-" + orderId + ".pdf", attachment.getValue().filename());
        assertEquals("application/pdf", attachment.getValue().contentType());
        assertArrayEquals(pdf, attachment.getValue().content());
    }

    @Test
    void shouldIgnoreEventWhoseStatusIsNotPaid() {
        useCase.execute(new OrderPaidEvent("not-evaluated", "PENDING", "not-evaluated"));

        verifyNoInteractions(orderQueryPort, userQueryPort, receiptGeneratorPort, emailSenderPort);
    }

    @Test
    void shouldRejectNullEvent() {
        assertThrows(NotificationException.class, () -> useCase.execute(null));
        verifyNoInteractions(orderQueryPort, userQueryPort, receiptGeneratorPort, emailSenderPort);
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
