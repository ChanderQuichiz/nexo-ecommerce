package com.nexo.ecommerce.notifications.application.usecases;

import com.nexo.ecommerce.notifications.application.ports.EmailSenderPort;
import com.nexo.ecommerce.notifications.application.ports.UserQueryPort;
import com.nexo.ecommerce.notifications.application.usecases.dto.OrderUpdateEvent;
import com.nexo.ecommerce.notifications.application.usecases.dto.UserResponse;
import com.nexo.ecommerce.notifications.domain.entities.Notification;
import com.nexo.ecommerce.notifications.domain.exceptions.NotificationException;
import com.nexo.ecommerce.notifications.domain.value_objects.CustomerId;
import com.nexo.ecommerce.notifications.domain.value_objects.EmailAddress;
import com.nexo.ecommerce.notifications.domain.value_objects.NotificationType;
import com.nexo.ecommerce.notifications.domain.value_objects.OrderId;

import java.util.UUID;

public class HandleOrderUpdatedUseCase {

    private final UserQueryPort userQueryPort;
    private final EmailSenderPort emailSenderPort;

    public HandleOrderUpdatedUseCase(UserQueryPort userQueryPort, EmailSenderPort emailSenderPort) {
        this.userQueryPort = userQueryPort;
        this.emailSenderPort = emailSenderPort;
    }

    public void execute(OrderUpdateEvent event) {
        if (event == null) {
            throw new NotificationException("Order update event must not be null");
        }

        StatusMessage message = messageFor(event.newStatus());
        if (message == null) {
            return;
        }

        OrderId orderId = new OrderId(UUID.fromString(event.orderId()));
        CustomerId customerId = new CustomerId(UUID.fromString(event.customerId()));
        UserResponse user = userQueryPort.getUserById(customerId.value());

        Notification notification = new Notification(
                orderId,
                customerId,
                new EmailAddress(user.email()),
                NotificationType.ORDER_UPDATED,
                message.subject(),
                "Hola " + user.name() + ", tu orden " + orderId.value() + " " + message.detail() + ".");

        emailSenderPort.sendSimpleEmail(
                notification.getRecipient(),
                notification.getSubject(),
                notification.getBody());
    }

    private StatusMessage messageFor(String status) {
        if (status == null) {
            return null;
        }

        return switch (status) {
            case "SHIPPED" -> new StatusMessage("Orden enviada", "fue enviada");
            case "DELIVERED" -> new StatusMessage("Orden entregada", "fue entregada");
            case "CANCELED" -> new StatusMessage("Orden cancelada", "fue cancelada");
            default -> null;
        };
    }

    private record StatusMessage(String subject, String detail) {
    }
}
