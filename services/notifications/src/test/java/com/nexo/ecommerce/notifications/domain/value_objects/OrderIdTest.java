package com.nexo.ecommerce.notifications.domain.value_objects;

import com.nexo.ecommerce.notifications.domain.exceptions.NotificationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderIdTest {

    @Test
    void shouldCreateOrderIdWithValidUuid() {
        UUID value = UUID.randomUUID();

        OrderId orderId = new OrderId(value);

        assertEquals(value, orderId.value());
    }

    @Test
    void shouldRejectNullUuid() {
        assertThrows(NotificationException.class, () -> new OrderId(null));
    }
}
