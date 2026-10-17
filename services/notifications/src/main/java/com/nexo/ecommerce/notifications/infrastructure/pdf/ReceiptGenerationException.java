package com.nexo.ecommerce.notifications.infrastructure.pdf;

public class ReceiptGenerationException extends RuntimeException {

    public ReceiptGenerationException(Throwable cause) {
        super("Unable to generate order receipt", cause);
    }
}
