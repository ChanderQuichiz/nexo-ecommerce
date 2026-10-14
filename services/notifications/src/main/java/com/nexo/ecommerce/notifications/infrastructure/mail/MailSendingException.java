package com.nexo.ecommerce.notifications.infrastructure.mail;

public class MailSendingException extends RuntimeException {

    public MailSendingException(Throwable cause) {
        super("Unable to send notification email", cause);
    }
}
