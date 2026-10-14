package com.nexo.ecommerce.notifications.domain.entities;

import com.nexo.ecommerce.notifications.domain.exceptions.NotificationException;
import com.nexo.ecommerce.notifications.domain.value_objects.CustomerId;
import com.nexo.ecommerce.notifications.domain.value_objects.EmailAddress;
import com.nexo.ecommerce.notifications.domain.value_objects.NotificationType;
import com.nexo.ecommerce.notifications.domain.value_objects.OrderId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NotificationTest {

    private OrderId orderId;
    private CustomerId customerId;
    private EmailAddress recipient;

    @BeforeEach
    void setUp() {
        orderId = new OrderId(UUID.randomUUID());
        customerId = new CustomerId(UUID.randomUUID());
        recipient = new EmailAddress("customer@example.com");
    }

    @Test
    void shouldCreateValidNotification() {
        Notification notification = new Notification(
                orderId,
                customerId,
                recipient,
                NotificationType.ORDER_CREATED,
                "Order created",
                "Your order was created successfully");

        assertEquals(orderId, notification.getOrderId());
        assertEquals(customerId, notification.getCustomerId());
        assertEquals(recipient, notification.getRecipient());
        assertEquals(NotificationType.ORDER_CREATED, notification.getType());
        assertEquals("Order created", notification.getSubject());
        assertEquals("Your order was created successfully", notification.getBody());
    }

    @Test
    void shouldRejectNullOrderId() {
        assertThrows(NotificationException.class, () -> createNotification(null, customerId, recipient,
                NotificationType.ORDER_CREATED, "Subject", "Body"));
    }

    @Test
    void shouldRejectNullCustomerId() {
        assertThrows(NotificationException.class, () -> createNotification(orderId, null, recipient,
                NotificationType.ORDER_CREATED, "Subject", "Body"));
    }

    @Test
    void shouldRejectNullRecipient() {
        assertThrows(NotificationException.class, () -> createNotification(orderId, customerId, null,
                NotificationType.ORDER_CREATED, "Subject", "Body"));
    }

    @Test
    void shouldRejectNullNotificationType() {
        assertThrows(NotificationException.class, () -> createNotification(orderId, customerId, recipient,
                null, "Subject", "Body"));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   "})
    void shouldRejectNullOrBlankSubject(String subject) {
        assertThrows(NotificationException.class, () -> createNotification(orderId, customerId, recipient,
                NotificationType.ORDER_CREATED, subject, "Body"));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " ", "   "})
    void shouldRejectNullOrBlankBody(String body) {
        assertThrows(NotificationException.class, () -> createNotification(orderId, customerId, recipient,
                NotificationType.ORDER_CREATED, "Subject", body));
    }

    private Notification createNotification(
            OrderId notificationOrderId,
            CustomerId notificationCustomerId,
            EmailAddress notificationRecipient,
            NotificationType notificationType,
            String subject,
            String body) {
        return new Notification(
                notificationOrderId,
                notificationCustomerId,
                notificationRecipient,
                notificationType,
                subject,
                body);
    }
}
