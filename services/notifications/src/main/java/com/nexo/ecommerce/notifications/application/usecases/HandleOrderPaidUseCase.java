package com.nexo.ecommerce.notifications.application.usecases;

import com.nexo.ecommerce.notifications.application.ports.EmailSenderPort;
import com.nexo.ecommerce.notifications.application.ports.OrderQueryPort;
import com.nexo.ecommerce.notifications.application.ports.ReceiptGeneratorPort;
import com.nexo.ecommerce.notifications.application.ports.UserQueryPort;
import com.nexo.ecommerce.notifications.application.usecases.dto.EmailAttachment;
import com.nexo.ecommerce.notifications.application.usecases.dto.OrderPaidEvent;
import com.nexo.ecommerce.notifications.application.usecases.dto.OrderResponse;
import com.nexo.ecommerce.notifications.application.usecases.dto.UserResponse;
import com.nexo.ecommerce.notifications.domain.entities.Notification;
import com.nexo.ecommerce.notifications.domain.exceptions.NotificationException;
import com.nexo.ecommerce.notifications.domain.value_objects.CustomerId;
import com.nexo.ecommerce.notifications.domain.value_objects.EmailAddress;
import com.nexo.ecommerce.notifications.domain.value_objects.NotificationType;
import com.nexo.ecommerce.notifications.domain.value_objects.OrderId;

import java.util.UUID;

public class HandleOrderPaidUseCase {

    private static final String PDF_CONTENT_TYPE = "application/pdf";

    private final OrderQueryPort orderQueryPort;
    private final UserQueryPort userQueryPort;
    private final ReceiptGeneratorPort receiptGeneratorPort;
    private final EmailSenderPort emailSenderPort;

    public HandleOrderPaidUseCase(
            OrderQueryPort orderQueryPort,
            UserQueryPort userQueryPort,
            ReceiptGeneratorPort receiptGeneratorPort,
            EmailSenderPort emailSenderPort) {
        this.orderQueryPort = orderQueryPort;
        this.userQueryPort = userQueryPort;
        this.receiptGeneratorPort = receiptGeneratorPort;
        this.emailSenderPort = emailSenderPort;
    }

    public void execute(OrderPaidEvent event) {
        if (event == null) {
            throw new NotificationException("Order paid event must not be null");
        }
        if (!"PAID".equals(event.status())) {
            return;
        }

        OrderId orderId = new OrderId(UUID.fromString(event.orderId()));
        CustomerId customerId = new CustomerId(UUID.fromString(event.customerId()));
        OrderResponse order = orderQueryPort.getOrderById(event.orderId());
        UserResponse user = userQueryPort.getUserById(customerId.value());
        byte[] receipt = receiptGeneratorPort.generateReceipt(order, user);

        Notification notification = new Notification(
                orderId,
                customerId,
                new EmailAddress(user.email()),
                NotificationType.ORDER_PAID,
                "Pago confirmado",
                "Hola " + user.name() + ", recibimos el pago de tu orden " + orderId.value() + ".");
        EmailAttachment attachment = new EmailAttachment(
                "receipt-" + event.orderId() + ".pdf",
                PDF_CONTENT_TYPE,
                receipt);

        emailSenderPort.sendEmailWithAttachment(
                notification.getRecipient(),
                notification.getSubject(),
                notification.getBody(),
                attachment);
    }
}
