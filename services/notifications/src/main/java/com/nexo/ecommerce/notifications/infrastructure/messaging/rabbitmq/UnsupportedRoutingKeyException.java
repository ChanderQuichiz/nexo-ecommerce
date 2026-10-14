package com.nexo.ecommerce.notifications.infrastructure.messaging.rabbitmq;

public class UnsupportedRoutingKeyException extends RuntimeException {

    public UnsupportedRoutingKeyException(String routingKey) {
        super("Unsupported Orders routing key: " + routingKey);
    }
}
