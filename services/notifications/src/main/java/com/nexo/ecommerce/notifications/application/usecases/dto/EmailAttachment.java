package com.nexo.ecommerce.notifications.application.usecases.dto;

import java.util.Objects;

public record EmailAttachment(
        String filename,
        String contentType,
        byte[] content) {

    public EmailAttachment {
        Objects.requireNonNull(filename, "Filename must not be null");
        Objects.requireNonNull(contentType, "Content type must not be null");
        content = Objects.requireNonNull(content, "Content must not be null").clone();
    }

    @Override
    public byte[] content() {
        return content.clone();
    }
}
