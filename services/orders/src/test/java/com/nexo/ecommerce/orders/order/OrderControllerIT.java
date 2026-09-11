package com.nexo.ecommerce.orders.order;

import com.nexo.ecommerce.orders.BaseIntegrationTest;
import com.nexo.ecommerce.orders.catalog_client.CatalogClient;
import com.nexo.ecommerce.orders.catalog_client.dto.GetProduct;
import com.nexo.ecommerce.orders.catalog_client.dto.ItemsValidateStock;
import com.nexo.ecommerce.orders.catalog_client.dto.ValidateStockRequest;
import com.nexo.ecommerce.orders.catalog_client.dto.ValidateStockResponse;
import com.nexo.ecommerce.orders.order.dto.CreateOrderItem;
import com.nexo.ecommerce.orders.order.dto.CreateOrderRequest;
import com.nexo.ecommerce.orders.order.dto.UpdateOrderStatusRequest;
import com.nexo.ecommerce.orders.order_items.OrderItemsRepository;
import com.nexo.ecommerce.orders.stripe.StripeService;
import com.stripe.model.PaymentIntent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OrderControllerIT extends BaseIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemsRepository orderItemsRepository;

    @MockBean
    private CatalogClient catalogClient;

    @MockBean
    private StripeService stripeService;

    @BeforeEach
    void setup() {
        orderItemsRepository.deleteAll();
        orderRepository.deleteAll();
    }

    @Test
    void deberiaCrearOrdenYRetornarSecretDeStripe() throws Exception {
        CreateOrderItem item = new CreateOrderItem(1L, 2);
        CreateOrderRequest request = new CreateOrderRequest(
                List.of(item),
                "Calle Falsa 123",
                "Springfield",
                "555-1234"
        );

        ValidateStockResponse stockResponse = new ValidateStockResponse(true, List.of(new ItemsValidateStock(1L, true, 10)));
        when(catalogClient.validateStock(any(ValidateStockRequest.class))).thenReturn(stockResponse);

        GetProduct product = new GetProduct(1L, "Producto Test", "Desc", new BigDecimal("100.00"), 10, "url", "Cat", true);
        when(catalogClient.getProductById(1L)).thenReturn(product);

        PaymentIntent paymentIntent = mock(PaymentIntent.class);
        when(paymentIntent.getId()).thenReturn("pi_123");
        when(paymentIntent.getClientSecret()).thenReturn("pi_secret_test_123");
        when(stripeService.createPaymentIntent(anyLong())).thenReturn(paymentIntent);

        webTestClient.post()
                .uri("/orders")
                .header("X-User-ID", "user-456")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("pi_secret_test_123");

        assertThat(orderRepository.count()).isEqualTo(1);
    }

    @Test
    void deberiaObtenerOrdenPorId() {
        OrderEntity order = new OrderEntity();
        order.setUserId("user-123");
        order.setTotal(new BigDecimal("150.00"));
        order.setStatus("PAID");
        orderRepository.save(order);

        webTestClient.get()
                .uri("/orders/" + order.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(order.getId())
                .jsonPath("$.userId").isEqualTo("user-123")
                .jsonPath("$.status").isEqualTo("PAID");
    }

    @Test
    void deberiaObtenerTodasLasOrdenes() {
        OrderEntity order1 = new OrderEntity();
        order1.setUserId("user-1");
        order1.setStatus("PAID");
        orderRepository.save(order1);

        OrderEntity order2 = new OrderEntity();
        order2.setUserId("user-2");
        order2.setStatus("PENDING");
        orderRepository.save(order2);

        webTestClient.get()
                .uri("/orders")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(2);
    }

    @Test
    void deberiaActualizarEstadoDeOrden() {
        OrderEntity order = new OrderEntity();
        order.setStatus("PENDING");
        orderRepository.save(order);

        UpdateOrderStatusRequest updateRequest = new UpdateOrderStatusRequest("SHIPPED");

        webTestClient.patch()
                .uri("/orders/" + order.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isNoContent();

        OrderEntity updatedOrder = orderRepository.findById(order.getId()).orElseThrow();
        assertThat(updatedOrder.getStatus()).isEqualTo("SHIPPED");
    }

    @Test
    void deberiaObtenerOrdenesDeUsuarioAutenticado() {
        String userId = "my-user-id";
        
        OrderEntity myOrder = new OrderEntity();
        myOrder.setUserId(userId);
        myOrder.setStatus("PAID");
        orderRepository.save(myOrder);

        OrderEntity otherOrder = new OrderEntity();
        otherOrder.setUserId("other-user");
        otherOrder.setStatus("PAID");
        orderRepository.save(otherOrder);

        webTestClient.get()
                .uri("/orders/me")
                .header("X-User-ID", userId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[0].userId").isEqualTo(userId);
    }
}
