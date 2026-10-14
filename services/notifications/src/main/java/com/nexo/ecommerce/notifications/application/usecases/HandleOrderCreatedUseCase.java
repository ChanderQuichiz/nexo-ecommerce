package com.nexo.ecommerce.notifications.application.usecases;

import com.nexo.ecommerce.notifications.application.ports.EmailSenderPort;
import com.nexo.ecommerce.notifications.application.ports.UserQueryPort;
import com.nexo.ecommerce.notifications.application.usecases.dto.OrderCreatedEvent;
import com.nexo.ecommerce.notifications.application.usecases.dto.UserResponse;
import com.nexo.ecommerce.notifications.domain.entities.Notification;
import com.nexo.ecommerce.notifications.domain.exceptions.NotificationException;
import com.nexo.ecommerce.notifications.domain.value_objects.CustomerId;
import com.nexo.ecommerce.notifications.domain.value_objects.EmailAddress;
import com.nexo.ecommerce.notifications.domain.value_objects.NotificationType;
import com.nexo.ecommerce.notifications.domain.value_objects.OrderId;

import java.util.UUID;

public class HandleOrderCreatedUseCase {

    private final UserQueryPort userQueryPort;
    private final EmailSenderPort emailSenderPort;

    public HandleOrderCreatedUseCase(UserQueryPort userQueryPort, EmailSenderPort emailSenderPort) {
        this.userQueryPort = userQueryPort;
        this.emailSenderPort = emailSenderPort;
    }

    public void execute(OrderCreatedEvent event) {
        if (event == null) {
            throw new NotificationException("Order created event must not be null");
        }

        OrderId orderId = new OrderId(UUID.fromString(event.orderId()));
        CustomerId customerId = new CustomerId(UUID.fromString(event.customerId()));
        UserResponse user = userQueryPort.getUserById(customerId.value());

        Notification notification = new Notification(
                orderId,
                customerId,
                new EmailAddress(user.email()),
                NotificationType.ORDER_CREATED,
                "Orden creada",
                "Hola " + user.name() + ", tu orden " + orderId.value() + " fue creada.");

        emailSenderPort.sendSimpleEmail(
                notification.getRecipient(),
                notification.getSubject(),
                notification.getBody());
    }
}
