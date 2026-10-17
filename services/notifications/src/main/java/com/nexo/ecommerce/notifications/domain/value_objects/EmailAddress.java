package com.nexo.ecommerce.notifications.domain.value_objects;

import com.nexo.ecommerce.notifications.domain.exceptions.NotificationException;

import java.util.Locale;
import java.util.regex.Pattern;

public record EmailAddress(String value) {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$",
            Pattern.CASE_INSENSITIVE);

    public EmailAddress {
        if (value == null) {
            throw new NotificationException("Email address must not be null");
        }

        value = value.trim().toLowerCase(Locale.ROOT);

        if (value.isBlank()) {
            throw new NotificationException("Email address must not be blank");
        }
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new NotificationException("Email address has an invalid format");
        }
    }
}
