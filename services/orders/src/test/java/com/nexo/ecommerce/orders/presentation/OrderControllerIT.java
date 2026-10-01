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
import com.nexo.ecommerce.orders.application.usecases.dto.CreateIntentPaymentResponse;
import com.nexo.ecommerce.orders.application.usecases.dto.OrdenCreateRequestDto;
import com.nexo.ecommerce.orders.application.usecases.dto.OrdenCreateResponseDto;
import com.nexo.ecommerce.orders.application.usecases.dto.OrderDto;
import com.nexo.ecommerce.orders.application.usecases.dto.UpdateStatusRequest;
import com.nexo.ecommerce.orders.application.usecases.dto.UpdateStatusResponse;

class OrderControllerIT extends BaseIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        when(catalogClient.validateStock(any())).thenReturn(new ValidateStockResponse(true, List.of()));
        when(paymentPort.createPaymentIntent(any(), any(), any())).thenReturn("pi_test_12345");
    }

    @Test
    @DisplayName("Debe crear una orden mediante POST /orders")
    void testCreateOrderIntegration() {
        OrdenCreateRequestDto request = new OrdenCreateRequestDto(
            "user-test-1",
            "Av. Universitaria 123",
            "Lima",
            "+519876543210",
            List.of(new OrdenCreateRequestDto.Item(UUID.randomUUID().toString(), new BigDecimal("150.00"), 1))
        );

        webTestClient.post()
            .uri("/orders")
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk()
            .expectBody(OrdenCreateResponseDto.class)
            .consumeWith(response -> {
                assertNotNull(response.getResponseBody());
                assertTrue(response.getResponseBody().message().startsWith("Orden creada exitosamente"));
            });
    }

    @Test
    @DisplayName("Debe listar todas las órdenes mediante GET /orders")
    void testGetAllOrdersIntegration() {
        OrdenCreateRequestDto request = new OrdenCreateRequestDto(
            "user-test-2",
            "Av. Javier Prado 456",
            "Lima",
            "+51911223344",
            List.of(new OrdenCreateRequestDto.Item(UUID.randomUUID().toString(), new BigDecimal("200.00"), 2))
        );
        webTestClient.post().uri("/orders").bodyValue(request).exchange().expectStatus().isOk();

        webTestClient.get()
            .uri("/orders")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(OrderDto.class)
            .consumeWith(response -> {
                assertNotNull(response.getResponseBody());
                assertTrue(response.getResponseBody().size() > 0);
            });
    }

    @Test
    @DisplayName("Debe listar órdenes por usuario mediante GET /orders/me")
    void testGetOrdersByUserIdIntegration() {
        String userId = "user-test-me-" + UUID.randomUUID();
        OrdenCreateRequestDto request = new OrdenCreateRequestDto(
            userId,
            "Calle Falsa 123",
            "Lima",
            "+51999888777",
            List.of(new OrdenCreateRequestDto.Item(UUID.randomUUID().toString(), new BigDecimal("50.00"), 1))
        );
        webTestClient.post().uri("/orders").bodyValue(request).exchange().expectStatus().isOk();

        webTestClient.get()
            .uri(uriBuilder -> uriBuilder.path("/orders/me").queryParam("userId", userId).build())
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(OrderDto.class)
            .consumeWith(response -> {
                assertNotNull(response.getResponseBody());
                assertEquals(1, response.getResponseBody().size());
                assertEquals(userId, response.getResponseBody().get(0).userId());
            });
    }

    @Test
    @DisplayName("Debe obtener una orden por ID mediante GET /orders/{id}")
    void testGetOrderByIdIntegration() {
        OrdenCreateRequestDto request = new OrdenCreateRequestDto(
            "user-test-id",
            "Av. Brasil 789",
            "Lima",
            "+51944556677",
            List.of(new OrdenCreateRequestDto.Item(UUID.randomUUID().toString(), new BigDecimal("300.00"), 1))
        );
        webTestClient.post().uri("/orders").bodyValue(request).exchange().expectStatus().isOk();

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

        webTestClient.get()
            .uri("/orders/" + orderId)
            .exchange()
            .expectStatus().isOk()
            .expectBody(OrderDto.class)
            .consumeWith(response -> {
                assertNotNull(response.getResponseBody());
                assertEquals(orderId, response.getResponseBody().id());
            });
    }

    @Test
    @DisplayName("Debe actualizar el estado de la orden mediante PATCH /orders/{id}/status")
    void testUpdateOrderStatusIntegration() {
        OrdenCreateRequestDto request = new OrdenCreateRequestDto(
            "user-test-status",
            "Av. Arequipa 999",
            "Lima",
            "+51933221144",
            List.of(new OrdenCreateRequestDto.Item(UUID.randomUUID().toString(), new BigDecimal("120.00"), 1))
        );
        webTestClient.post().uri("/orders").bodyValue(request).exchange().expectStatus().isOk();

        List<OrderDto> orders = webTestClient.get()
            .uri("/orders")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(OrderDto.class)
            .returnResult()
            .getResponseBody();

        assertNotNull(orders);
        String orderId = orders.get(0).id();

        UpdateStatusRequest updateRequest = new UpdateStatusRequest("PAID", orderId);

        webTestClient.patch()
            .uri("/orders/" + orderId + "/status")
            .bodyValue(updateRequest)
            .exchange()
            .expectStatus().isOk()
            .expectBody(UpdateStatusResponse.class)
            .consumeWith(response -> {
                assertNotNull(response.getResponseBody());
                assertNotNull(response.getResponseBody().message());
            });
    }

    @Test
    @DisplayName("Debe crear intent de pago mediante POST /orders/intent-payment")
    void testCreateIntentPaymentIntegration() {
        OrdenCreateRequestDto request = new OrdenCreateRequestDto(
            "user-test-payment",
            "Av. Larco 100",
            "Lima",
            "+51922334455",
            List.of(new OrdenCreateRequestDto.Item(UUID.randomUUID().toString(), new BigDecimal("500.00"), 1))
        );
        webTestClient.post().uri("/orders").bodyValue(request).exchange().expectStatus().isOk();

        List<OrderDto> orders = webTestClient.get()
            .uri("/orders")
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(OrderDto.class)
            .returnResult()
            .getResponseBody();

        assertNotNull(orders);
        String orderId = orders.get(0).id();

        CreateIntentPaymentRequest paymentRequest = new CreateIntentPaymentRequest(orderId);

        webTestClient.post()
            .uri("/orders/intent-payment")
            .bodyValue(paymentRequest)
            .exchange()
            .expectStatus().isOk()
            .expectBody(CreateIntentPaymentResponse.class)
            .consumeWith(response -> {
                assertNotNull(response.getResponseBody());
                assertNotNull(response.getResponseBody().intentId());
            });
    }
}
