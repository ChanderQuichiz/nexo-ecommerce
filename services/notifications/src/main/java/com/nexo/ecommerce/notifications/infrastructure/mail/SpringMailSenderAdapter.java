package com.nexo.ecommerce.notifications.infrastructure.mail;

import com.nexo.ecommerce.notifications.application.ports.EmailSenderPort;
import com.nexo.ecommerce.notifications.application.usecases.dto.EmailAttachment;
import com.nexo.ecommerce.notifications.domain.value_objects.EmailAddress;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class SpringMailSenderAdapter implements EmailSenderPort {

    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    public SpringMailSenderAdapter(JavaMailSender mailSender, MailProperties mailProperties) {
        this.mailSender = mailSender;
        this.mailProperties = mailProperties;
    }

    @Override
    public void sendSimpleEmail(EmailAddress recipient, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    false,
                    StandardCharsets.UTF_8.name());
            populateMessage(helper, recipient, subject, body);
            mailSender.send(message);
        } catch (MessagingException | MailException exception) {
            throw new MailSendingException(exception);
        }
    }

    @Override
    public void sendEmailWithAttachment(
            EmailAddress recipient,
            String subject,
            String body,
            EmailAttachment attachment) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED,
                    StandardCharsets.UTF_8.name());
            populateMessage(helper, recipient, subject, body);
            helper.addAttachment(
                    attachment.filename(),
                    new ByteArrayResource(attachment.content()),
                    attachment.contentType());
            mailSender.send(message);
        } catch (MessagingException | MailException exception) {
            throw new MailSendingException(exception);
        }
    }

    private void populateMessage(
            MimeMessageHelper helper,
            EmailAddress recipient,
            String subject,
            String body) throws MessagingException {
        helper.setFrom(mailProperties.from());
        helper.setTo(recipient.value());
        helper.setSubject(subject);
        helper.setText(body, false);
    }
}
