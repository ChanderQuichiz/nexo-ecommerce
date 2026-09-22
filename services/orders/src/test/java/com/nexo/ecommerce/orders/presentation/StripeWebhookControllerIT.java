package com.nexo.ecommerce.orders.presentation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.nexo.ecommerce.orders.BaseIntegrationTest;
import com.nexo.ecommerce.orders.application.client.dto.ValidateStockResponse;
import com.nexo.ecommerce.orders.application.usecases.dto.CreateIntentPaymentRequest;
import com.nexo.ecommerce.orders.application.usecases.dto.OrdenCreateRequestDto;
import com.nexo.ecommerce.orders.application.usecases.dto.OrderDto;

class StripeWebhookControllerIT extends BaseIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        when(catalogClient.validateStock(any())).thenReturn(new ValidateStockResponse(true, List.of()));
        when(paymentPort.createPaymentIntent(any(), any(), any())).thenReturn("pi_webhook_test_123");
    }

    @Test
    @DisplayName("Debe procesar webhook de Stripe payment_intent.succeeded mediante POST /webhooks/stripe")
    void testStripeWebhookSuccessIntegration() {
        // 1. Create an order
        OrdenCreateRequestDto createRequest = new OrdenCreateRequestDto(
            "user-webhook-1",
            "Av. Larco 500",
            "Lima",
            "+51988776655",
            List.of(new OrdenCreateRequestDto.Item(UUID.randomUUID().toString(), new BigDecimal("250.00"), 1))
        );
        webTestClient.post().uri("/orders").bodyValue(createRequest).exchange().expectStatus().isOk();

        // 2. Get created order ID
        List<OrderDto> orders = webTestClient.get()
            .uri("/orders")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(OrderDto.class)
            .returnResult()
            .getResponseBody();

        assertNotNull(orders);
        assertFalse(orders.isEmpty());
        String orderId = orders.get(0).id();

        // 3. Create payment intent for the order so paymentIntentId is associated
        CreateIntentPaymentRequest paymentRequest = new CreateIntentPaymentRequest(orderId);
        webTestClient.post()
            .uri("/orders/intent-payment")
            .bodyValue(paymentRequest)
            .exchange()
            .expectStatus().isOk();

        // 4. Simulate Stripe webhook payload for payment_intent.succeeded
        String webhookPayload = "{"
            + "\"id\": \"evt_123\","
            + "\"object\": \"event\","
            + "\"type\": \"payment_intent.succeeded\","
            + "\"data\": {"
            + "    \"object\": {"
            + "        \"id\": \"pi_webhook_test_123\","
            + "        \"object\": \"payment_intent\","
            + "        \"status\": \"succeeded\""
            + "    }"
            + "}"
            + "}";

        webTestClient.post()
            .uri("/webhooks/stripe")
            .header("Stripe-Signature", "t=123,v1=abc")
            .bodyValue(webhookPayload)
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class)
            .consumeWith(response -> {
                assertEquals("Webhook received successfully", response.getResponseBody());
            });

        // 5. Verify order status was updated to PAID
        webTestClient.get()
            .uri("/orders/" + orderId)
            .exchange()
            .expectStatus().isOk()
            .expectBody(OrderDto.class)
            .consumeWith(response -> {
                assertNotNull(response.getResponseBody());
                assertEquals("PAID", response.getResponseBody().status());
            });
    }
}
