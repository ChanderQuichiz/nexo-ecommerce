package com.nexo.ecommerce.notifications.infrastructure.mail;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "notifications.mail")
public record MailProperties(String from) {
}
