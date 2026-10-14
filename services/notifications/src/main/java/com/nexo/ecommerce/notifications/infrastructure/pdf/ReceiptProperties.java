package com.nexo.ecommerce.notifications.infrastructure.pdf;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "notifications.receipt")
public record ReceiptProperties(String currency) {
}
