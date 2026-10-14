package com.nexo.ecommerce.notifications.application.usecases;

import com.nexo.ecommerce.notifications.application.ports.OrderQueryPort;
import com.nexo.ecommerce.notifications.application.ports.ReceiptGeneratorPort;
import com.nexo.ecommerce.notifications.application.ports.UserQueryPort;
import com.nexo.ecommerce.notifications.application.usecases.dto.OrderResponse;
import com.nexo.ecommerce.notifications.application.usecases.dto.UserResponse;
import com.nexo.ecommerce.notifications.domain.exceptions.NotificationException;
import com.nexo.ecommerce.notifications.domain.exceptions.OrderNotPaidException;
import com.nexo.ecommerce.notifications.domain.exceptions.UnsupportedOrderStatusException;

import java.util.Set;
import java.util.UUID;

public class GenerateReceiptUseCase {

    private static final Set<String> RECEIPT_STATUSES = Set.of("PAID", "SHIPPED", "DELIVERED");
    private static final Set<String> UNPAID_STATUSES = Set.of("CREATED", "PENDING");

    private final OrderQueryPort orderQueryPort;
    private final UserQueryPort userQueryPort;
    private final ReceiptGeneratorPort receiptGeneratorPort;

    public GenerateReceiptUseCase(
            OrderQueryPort orderQueryPort,
            UserQueryPort userQueryPort,
            ReceiptGeneratorPort receiptGeneratorPort) {
        this.orderQueryPort = orderQueryPort;
        this.userQueryPort = userQueryPort;
        this.receiptGeneratorPort = receiptGeneratorPort;
    }

    public byte[] execute(String orderId) {
        if (orderId == null || orderId.isBlank()) {
            throw new NotificationException("Order ID must not be blank");
        }

        OrderResponse order = orderQueryPort.getOrderById(orderId);
        validateStatus(order);
        UserResponse user = userQueryPort.getUserById(UUID.fromString(order.userId()));
        return receiptGeneratorPort.generateReceipt(order, user);
    }

    private void validateStatus(OrderResponse order) {
        String status = order.status();
        if (RECEIPT_STATUSES.contains(status)) {
            return;
        }
        if (UNPAID_STATUSES.contains(status)) {
            throw new OrderNotPaidException(order.id(), status);
        }
        throw new UnsupportedOrderStatusException(status);
    }
}
