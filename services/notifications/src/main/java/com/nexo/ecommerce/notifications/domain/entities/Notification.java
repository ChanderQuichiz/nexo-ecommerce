package com.nexo.ecommerce.notifications.domain.entities;

import com.nexo.ecommerce.notifications.domain.exceptions.NotificationException;
import com.nexo.ecommerce.notifications.domain.value_objects.CustomerId;
import com.nexo.ecommerce.notifications.domain.value_objects.EmailAddress;
import com.nexo.ecommerce.notifications.domain.value_objects.NotificationType;
import com.nexo.ecommerce.notifications.domain.value_objects.OrderId;

public final class Notification {

    private final OrderId orderId;
    private final CustomerId customerId;
    private final EmailAddress recipient;
    private final NotificationType type;
    private final String subject;
    private final String body;

    public Notification(
            OrderId orderId,
            CustomerId customerId,
            EmailAddress recipient,
            NotificationType type,
            String subject,
            String body) {
        this.orderId = requireNonNull(orderId, "Order ID");
        this.customerId = requireNonNull(customerId, "Customer ID");
        this.recipient = requireNonNull(recipient, "Recipient");
        this.type = requireNonNull(type, "Notification type");
        this.subject = requireText(subject, "Subject");
        this.body = requireText(body, "Body");
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public EmailAddress getRecipient() {
        return recipient;
    }

    public NotificationType getType() {
        return type;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    private static <T> T requireNonNull(T value, String fieldName) {
        if (value == null) {
            throw new NotificationException(fieldName + " must not be null");
        }
        return value;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null) {
            throw new NotificationException(fieldName + " must not be null");
        }
        if (value.isBlank()) {
            throw new NotificationException(fieldName + " must not be blank");
        }
        return value;
    }
}
