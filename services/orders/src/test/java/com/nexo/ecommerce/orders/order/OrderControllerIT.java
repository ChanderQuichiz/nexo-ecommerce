package com.nexo.ecommerce.orders.order;

import com.nexo.ecommerce.orders.BaseIntegrationTest;
import com.nexo.ecommerce.orders.catalog_client.CatalogClient;
import com.nexo.ecommerce.orders.catalog_client.dto.GetProduct;
import com.nexo.ecommerce.orders.catalog_client.dto.ItemsValidateStock;
import com.nexo.ecommerce.orders.catalog_client.dto.ValidateStockRequest;
import com.nexo.ecommerce.orders.catalog_client.dto.ValidateStockResponse;
import com.nexo.ecommerce.orders.order.dto.CreateOrderItem;
import com.nexo.ecommerce.orders.order.dto.CreateOrderRequest;
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
        order.setSubtotal(new BigDecimal("100.00"));
        order.setShippingFee(new BigDecimal("10.00"));
        order.setTax(new BigDecimal("18.00"));
        order.setTotal(new BigDecimal("128.00"));
        order.setStatus("PAID");
        order.setAddress("Calle Falsa 123");
        order.setCity("Springfield");
        order.setPhone("555-1234");
        orderRepository.save(order);

        webTestClient.get()
                .uri("/orders/" + order.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(order.getId())
                .jsonPath("$.userId").isEqualTo("user-123");
    }

    @Test
    void deberiaObtenerTodasLasOrdenes() {
        OrderEntity order = new OrderEntity();
        order.setUserId("user-1");
        order.setSubtotal(new BigDecimal("100.00"));
        order.setShippingFee(new BigDecimal("10.00"));
        order.setTax(new BigDecimal("18.00"));
        order.setTotal(new BigDecimal("128.00"));
        order.setStatus("PAID");
        order.setAddress("Address"); order.setCity("City"); order.setPhone("123");
        orderRepository.save(order);

        webTestClient.get()
                .uri("/orders")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(1);
    }
}
