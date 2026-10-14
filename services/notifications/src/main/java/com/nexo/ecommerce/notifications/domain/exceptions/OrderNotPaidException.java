package com.nexo.ecommerce.notifications.domain.exceptions;

public class OrderNotPaidException extends NotificationException {

    public OrderNotPaidException(String orderId, String status) {
        super("Order " + orderId + " is not paid; current status: " + status);
    }
}
