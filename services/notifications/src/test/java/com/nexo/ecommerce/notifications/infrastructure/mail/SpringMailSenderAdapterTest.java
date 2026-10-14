package com.nexo.ecommerce.notifications.infrastructure.mail;

import com.nexo.ecommerce.notifications.application.usecases.dto.EmailAttachment;
import com.nexo.ecommerce.notifications.domain.value_objects.EmailAddress;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpringMailSenderAdapterTest {

    private static final String FROM = "no-reply@nexo-ecommerce.com";
    private static final String TO = "customer@example.com";

    @Mock
    private JavaMailSender mailSender;

    private SpringMailSenderAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new SpringMailSenderAdapter(mailSender, new MailProperties(FROM));
    }

    @Test
    void shouldSendUtf8SimpleEmailWithoutAttachment() throws Exception {
        MimeMessage message = inMemoryMessage();
        when(mailSender.createMimeMessage()).thenReturn(message);

        adapter.sendSimpleEmail(
                new EmailAddress(TO),
                "Orden enviada",
                "Tu orden fue enviada correctamente.");

        MimeMessage sent = captureSentMessage();
        sent.saveChanges();
        assertEquals(FROM, sent.getFrom()[0].toString());
        assertEquals(TO, sent.getRecipients(Message.RecipientType.TO)[0].toString());
        assertEquals("Orden enviada", sent.getSubject());
        assertEquals("Tu orden fue enviada correctamente.", sent.getContent());
        assertTrue(sent.getContentType().contains("charset=UTF-8"));
        assertFalse(sent.isMimeType("multipart/*"));
    }

    @Test
    void shouldSendMultipartEmailWithPdfAttachment() throws Exception {
        byte[] pdf = "%PDF-test-content".getBytes(StandardCharsets.UTF_8);
        MimeMessage message = inMemoryMessage();
        when(mailSender.createMimeMessage()).thenReturn(message);

        adapter.sendEmailWithAttachment(
                new EmailAddress(TO),
                "Pago confirmado",
                "Adjuntamos tu comprobante.",
                new EmailAttachment("receipt-order-1.pdf", "application/pdf", pdf));

        MimeMessage sent = captureSentMessage();
        sent.saveChanges();
        assertEquals(FROM, sent.getFrom()[0].toString());
        assertEquals(TO, sent.getRecipients(Message.RecipientType.TO)[0].toString());
        assertEquals("Pago confirmado", sent.getSubject());
        assertTrue(sent.isMimeType("multipart/*"));

        MimeMultipart multipart = (MimeMultipart) sent.getContent();
        assertEquals(2, multipart.getCount());
        assertEquals("Adjuntamos tu comprobante.", multipart.getBodyPart(0).getContent());
        assertEquals("receipt-order-1.pdf", multipart.getBodyPart(1).getFileName());
        assertTrue(multipart.getBodyPart(1).getContentType().startsWith("application/pdf"));
        assertArrayEquals(pdf, multipart.getBodyPart(1).getInputStream().readAllBytes());
    }

    @Test
    void shouldTranslateMailSenderFailureAndPreserveCause() {
        MimeMessage message = inMemoryMessage();
        MailSendException cause = new MailSendException("SMTP failure");
        when(mailSender.createMimeMessage()).thenReturn(message);
        doThrow(cause).when(mailSender).send(any(MimeMessage.class));

        MailSendingException exception = assertThrows(
                MailSendingException.class,
                () -> adapter.sendSimpleEmail(
                        new EmailAddress(TO),
                        "Subject",
                        "Body"));

        assertSame(cause, exception.getCause());
        assertEquals("Unable to send notification email", exception.getMessage());
    }

    private MimeMessage inMemoryMessage() {
        return new MimeMessage(Session.getInstance(new Properties()));
    }

    private MimeMessage captureSentMessage() {
        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(captor.capture());
        return captor.getValue();
    }
}
