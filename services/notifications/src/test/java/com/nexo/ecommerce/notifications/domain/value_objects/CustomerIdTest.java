package com.nexo.ecommerce.notifications.domain.value_objects;

import com.nexo.ecommerce.notifications.domain.exceptions.NotificationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CustomerIdTest {

    @Test
    void shouldCreateCustomerIdWithValidUuid() {
        UUID value = UUID.randomUUID();

        CustomerId customerId = new CustomerId(value);

        assertEquals(value, customerId.value());
    }

    @Test
    void shouldRejectNullUuid() {
        assertThrows(NotificationException.class, () -> new CustomerId(null));
    }
}
