package com.nexo.ecommerce.notifications.domain.exceptions;

public class UnsupportedOrderStatusException extends NotificationException {

    public UnsupportedOrderStatusException(String status) {
        super("Unsupported order status: " + status);
    }
}
