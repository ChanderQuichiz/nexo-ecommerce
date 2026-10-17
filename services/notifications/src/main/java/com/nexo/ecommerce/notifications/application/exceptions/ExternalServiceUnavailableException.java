package com.nexo.ecommerce.notifications.application.exceptions;

public class ExternalServiceUnavailableException extends RuntimeException {

    public ExternalServiceUnavailableException(String serviceName, Throwable cause) {
        super(serviceName + " is unavailable", cause);
    }
}
