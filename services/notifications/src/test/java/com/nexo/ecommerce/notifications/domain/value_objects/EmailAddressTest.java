package com.nexo.ecommerce.notifications.domain.value_objects;

import com.nexo.ecommerce.notifications.domain.exceptions.NotificationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmailAddressTest {

    @Test
    void shouldNormalizeValidEmailAddress() {
        EmailAddress email = new EmailAddress("  Customer.Name@Example.COM  ");

        assertEquals("customer.name@example.com", email.value());
    }

    @Test
    void shouldRejectNullEmailAddress() {
        assertThrows(NotificationException.class, () -> new EmailAddress(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   "})
    void shouldRejectBlankEmailAddress(String value) {
        assertThrows(NotificationException.class, () -> new EmailAddress(value));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "invalid",
            "missing-at.example.com",
            "missing-domain@",
            "@missing-local.com",
            "customer@example"
    })
    void shouldRejectInvalidEmailAddress(String value) {
        assertThrows(NotificationException.class, () -> new EmailAddress(value));
    }
}
