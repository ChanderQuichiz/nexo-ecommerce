package com.nexo.ecommerce.auth.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth.admin")
public record AdminBootstrapProperties(String name, String email, String password) {
    public boolean isConfigured() {
        boolean hasEmail = email != null && !email.isBlank();
        boolean hasPassword = password != null && !password.isBlank();
        if (hasEmail != hasPassword) {
            throw new IllegalStateException(
                    "AUTH_ADMIN_EMAIL and AUTH_ADMIN_PASSWORD must be configured together");
        }
        return hasEmail;
    }
}
