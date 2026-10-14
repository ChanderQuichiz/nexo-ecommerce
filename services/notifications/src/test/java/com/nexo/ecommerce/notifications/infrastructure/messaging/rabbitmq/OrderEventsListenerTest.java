package com.nexo.ecommerce.notifications.infrastructure.messaging.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexo.ecommerce.notifications.application.usecases.HandleOrderCreatedUseCase;
import com.nexo.ecommerce.notifications.application.usecases.HandleOrderPaidUseCase;
import com.nexo.ecommerce.notifications.application.usecases.HandleOrderUpdatedUseCase;
import com.nexo.ecommerce.notifications.application.usecases.dto.OrderCreatedEvent;
import com.nexo.ecommerce.notifications.application.usecases.dto.OrderPaidEvent;
import com.nexo.ecommerce.notifications.application.usecases.dto.OrderUpdateEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class OrderEventsListenerTest {

    private static final String CREATED_ROUTING_KEY = "orders.created";
    private static final String UPDATED_ROUTING_KEY = "orders.updated";
    private static final String PAID_ROUTING_KEY = "orders.paid";

    @Mock
    private HandleOrderCreatedUseCase handleOrderCreatedUseCase;

    @Mock
    private HandleOrderUpdatedUseCase handleOrderUpdatedUseCase;

    @Mock
    private HandleOrderPaidUseCase handleOrderPaidUseCase;

    private OrderEventsListener listener;

    @BeforeEach
    void setUp() {
        listener = new OrderEventsListener(
                new ObjectMapper(),
                handleOrderCreatedUseCase,
                handleOrderUpdatedUseCase,
                handleOrderPaidUseCase,
                CREATED_ROUTING_KEY,
                UPDATED_ROUTING_KEY,
                PAID_ROUTING_KEY);
    }

    @Test
    void shouldDeserializeCreatedEventAndDelegateOnlyToCreatedUseCase() throws Exception {
        String json = """
                {"orderId":"order-1","customerId":"customer-1","status":"CREATED"}
                """;

        listener.consume(message(json, CREATED_ROUTING_KEY, true));

        ArgumentCaptor<OrderCreatedEvent> event = ArgumentCaptor.forClass(OrderCreatedEvent.class);
        verify(handleOrderCreatedUseCase).execute(event.capture());
        assertEquals("order-1", event.getValue().orderId());
        assertEquals("customer-1", event.getValue().customerId());
        assertEquals("CREATED", event.getValue().status());
        verifyNoInteractions(handleOrderUpdatedUseCase, handleOrderPaidUseCase);
    }

    @Test
    void shouldDeserializeUpdatedEventAndDelegateOnlyToUpdatedUseCase() throws Exception {
        String json = """
                {"orderId":"order-2","newStatus":"SHIPPED","customerId":"customer-2"}
                """;

        listener.consume(message(json, UPDATED_ROUTING_KEY, false));

        ArgumentCaptor<OrderUpdateEvent> event = ArgumentCaptor.forClass(OrderUpdateEvent.class);
        verify(handleOrderUpdatedUseCase).execute(event.capture());
        assertEquals("order-2", event.getValue().orderId());
        assertEquals("SHIPPED", event.getValue().newStatus());
        assertEquals("customer-2", event.getValue().customerId());
        verifyNoInteractions(handleOrderCreatedUseCase, handleOrderPaidUseCase);
    }

    @Test
    void shouldDeserializePaidEventAndDelegateOnlyToPaidUseCase() throws Exception {
        String json = """
                {"orderId":"order-3","status":"PAID","customerId":"customer-3"}
                """;

        listener.consume(message(json, PAID_ROUTING_KEY, false));

        ArgumentCaptor<OrderPaidEvent> event = ArgumentCaptor.forClass(OrderPaidEvent.class);
        verify(handleOrderPaidUseCase).execute(event.capture());
        assertEquals("order-3", event.getValue().orderId());
        assertEquals("PAID", event.getValue().status());
        assertEquals("customer-3", event.getValue().customerId());
        verifyNoInteractions(handleOrderCreatedUseCase, handleOrderUpdatedUseCase);
    }

    @Test
    void shouldRejectUnsupportedRoutingKeyWithoutDelegating() {
        Message message = message("{}", "orders.unknown", false);

        assertThrows(UnsupportedRoutingKeyException.class, () -> listener.consume(message));

        verifyNoInteractions(handleOrderCreatedUseCase, handleOrderUpdatedUseCase, handleOrderPaidUseCase);
    }

    @Test
    void shouldPropagateInvalidJsonWithoutDelegating() {
        Message message = message("{invalid-json", CREATED_ROUTING_KEY, false);

        assertThrows(JsonProcessingException.class, () -> listener.consume(message));

        verifyNoInteractions(handleOrderCreatedUseCase, handleOrderUpdatedUseCase, handleOrderPaidUseCase);
    }

    private Message message(String json, String routingKey, boolean includeOrdersTypeHeader) {
        MessageProperties properties = new MessageProperties();
        properties.setReceivedRoutingKey(routingKey);
        if (includeOrdersTypeHeader) {
            properties.setHeader(
                    "__TypeId__",
                    "com.nexo.ecommerce.orders.domain.events.OrderCreatedEvent");
        }
        return new Message(json.getBytes(StandardCharsets.UTF_8), properties);
    }
}
