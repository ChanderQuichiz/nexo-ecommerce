package com.nexo.ecommerce.notifications.application.ports;

import com.nexo.ecommerce.notifications.application.usecases.dto.EmailAttachment;
import com.nexo.ecommerce.notifications.domain.value_objects.EmailAddress;

public interface EmailSenderPort {

    void sendSimpleEmail(EmailAddress recipient, String subject, String body);

    void sendEmailWithAttachment(
            EmailAddress recipient,
            String subject,
            String body,
            EmailAttachment attachment);
}
