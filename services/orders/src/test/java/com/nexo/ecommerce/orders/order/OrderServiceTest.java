package com.nexo.ecommerce.orders.order;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexo.ecommerce.orders.catalog_client.CatalogClient;
import com.nexo.ecommerce.orders.catalog_client.dto.GetProduct;
import com.nexo.ecommerce.orders.catalog_client.dto.ItemsValidateStock;
import com.nexo.ecommerce.orders.catalog_client.dto.ValidateStockRequest;
import com.nexo.ecommerce.orders.catalog_client.dto.ValidateStockResponse;
import com.nexo.ecommerce.orders.order.dto.CreateOrderItem;
import com.nexo.ecommerce.orders.order.dto.CreateOrderRequest;
import com.nexo.ecommerce.orders.order.dto.GetOrderResponse;
import com.nexo.ecommerce.orders.order.dto.OrderItem;
import com.nexo.ecommerce.orders.order.dto.OrderItemRow;
import com.nexo.ecommerce.orders.order_items.OrderItemsEntity;
import com.nexo.ecommerce.orders.order_items.OrderItemsRepository;
import com.nexo.ecommerce.orders.stripe.StripeService;
import com.stripe.model.PaymentIntent;


@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemsRepository orderItemsRepository;

    @Mock
    private CatalogClient catalogClient;

    @Mock
    private StripeService stripeService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OrderService orderService;

    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        // Inyección de valores de configuración @Value
        ReflectionTestUtils.setField(orderService, "interestRate", new BigDecimal("0.18"));
        ReflectionTestUtils.setField(orderService, "shippingFee", new BigDecimal("10.00"));
    }

    @Nested
    @DisplayName("Pruebas para getOrderById")
    class GetOrderByIdTests {

        @Test
        @DisplayName("Debe retornar la orden con sus ítems")
        void getOrderById_Success() {
            String orderId = "ord-123";
            GetOrderResponse mockOrder = new GetOrderResponse(
                    orderId, "user-1", new BigDecimal("100.00"), new BigDecimal("10.00"),
                    new BigDecimal("18.00"), new BigDecimal("128.00"), "PAID", now,
                    new ArrayList<>(), "Av. Lima 123", "Lima", "987654321"
            );
            List<OrderItem> mockItems = List.of(
                    new OrderItem(1L, "Laptop", new BigDecimal("100.00"), 1)
            );

            when(orderRepository.findOrderById(orderId)).thenReturn(mockOrder);
            when(orderItemsRepository.findItemsByOrderId(orderId)).thenReturn(mockItems);

            GetOrderResponse response = orderService.getOrderById(orderId);

            assertNotNull(response);
            assertEquals(orderId, response.getId());
            assertEquals(now, response.getDate());
            assertEquals(1, response.getItems().size());
            assertEquals("Laptop", response.getItems().get(0).name());
        }
    }

    @Nested
    @DisplayName("Pruebas para getAllOrders y getOrdersByUserId")
    class GetMultipleOrdersTests {

        @Test
        @DisplayName("getAllOrders debe mapear correctamente los ítems")
        void getAllOrders_Success() {
            GetOrderResponse order1 = new GetOrderResponse("ord-1", "user-1", BigDecimal.TEN, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.TEN, "PAID", now, new ArrayList<>(), "Addr1", "City1", "123");
            GetOrderResponse order2 = new GetOrderResponse("ord-2", "user-2", BigDecimal.TEN, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.TEN, "PAID", now, new ArrayList<>(), "Addr2", "City2", "456");

            List<GetOrderResponse> orders = List.of(order1, order2);
            List<OrderItemRow> items = List.of(
                    new OrderItemRow(1L, "Mouse", new BigDecimal("10.00"), 1, "ord-1"),
                    new OrderItemRow(2L, "Teclado", new BigDecimal("20.00"), 1, "ord-2")
            );

            when(orderRepository.getAllOrders()).thenReturn(orders);
            when(orderItemsRepository.findItemsByOrderIds(List.of("ord-1", "ord-2"))).thenReturn(items);

            List<GetOrderResponse> result = orderService.getAllOrders();

            assertEquals(2, result.size());
            assertEquals(1, result.get(0).getItems().size());
            assertEquals("Mouse", result.get(0).getItems().get(0).name());
            assertEquals(1, result.get(1).getItems().size());
            assertEquals("Teclado", result.get(1).getItems().get(0).name());
        }

        @Test
        @DisplayName("getOrdersByUserId debe mapear los ítems para un usuario")
        void getOrdersByUserId_Success() {
            String userId = "user-1";
            GetOrderResponse order1 = new GetOrderResponse("ord-1", userId, BigDecimal.TEN, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.TEN, "PAID", now, new ArrayList<>(), "Addr1", "City1", "123");

            when(orderRepository.getAllOrdersByUserId(userId)).thenReturn(List.of(order1));
            when(orderItemsRepository.findItemsByOrderIds(List.of("ord-1"))).thenReturn(List.of());

            List<GetOrderResponse> result = orderService.getOrdersByUserId(userId);

            assertEquals(1, result.size());
            verify(orderRepository).getAllOrdersByUserId(userId);
        }
    }

    @Nested
    @DisplayName("Pruebas para createOrder")
    class CreateOrderTests {

        @Test
        @DisplayName("Debe crear la orden e invocar a Stripe con el monto en centavos")
        void createOrder_Success() throws Exception {
            String userId = "user-1";
            CreateOrderItem itemInput = new CreateOrderItem(1L, 2);
            CreateOrderRequest request = new CreateOrderRequest(List.of(itemInput), "Av. Central", "Lima", "999888777");

            ValidateStockResponse stockResponse = new ValidateStockResponse(true, List.of(new ItemsValidateStock(1L, true, 10)));
            when(catalogClient.validateStock(any(ValidateStockRequest.class))).thenReturn(stockResponse);

            GetProduct productResponse = new GetProduct(1L, "Mouse", "A great mouse", new BigDecimal("100.00"), 10, "http://image.url", "Electronics", true);
            when(catalogClient.getProductById(1L)).thenReturn(productResponse);

            PaymentIntent mockPaymentIntent = mock(PaymentIntent.class);
            when(mockPaymentIntent.getId()).thenReturn("pi_mock_123");
            when(mockPaymentIntent.getClientSecret()).thenReturn("pi_mock_123_secret_abc");

            when(stripeService.createPaymentIntent(24600L)).thenReturn(mockPaymentIntent);

            String clientSecret = orderService.createOrder(userId, request);

            assertEquals("pi_mock_123_secret_abc", clientSecret);
            verify(orderRepository, times(2)).save(any(OrderEntity.class));
            verify(orderItemsRepository, times(1)).save(any(OrderItemsEntity.class));
            verify(stripeService).createPaymentIntent(24600L);
        }

        @Test
        @DisplayName("Debe lanzar CONFLICT (409) cuando no hay stock suficiente")
        void createOrder_OutOfStock_ThrowsConflict() {
            String userId = "user-1";
            CreateOrderItem itemInput = new CreateOrderItem(1L, 5);
            CreateOrderRequest request = new CreateOrderRequest(List.of(itemInput), "Av. Central", "Lima", "999888777");

            // Configurar el mock para retornar la respuesta indicando que NO hay stock
            ValidateStockResponse stockResponse = new ValidateStockResponse(false, List.of(new ItemsValidateStock(1L, false, 0)));
            when(catalogClient.validateStock(any(ValidateStockRequest.class))).thenReturn(stockResponse);

            ResponseStatusException exception = assertThrows(
                    ResponseStatusException.class,
                    () -> orderService.createOrder(userId, request)
            );

            assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
            verifyNoInteractions(stripeService);
        }

        @Test
        @DisplayName("Debe lanzar PAYMENT_REQUIRED (402) si falla Stripe")
        void createOrder_StripeError_ThrowsPaymentRequired() throws Exception {
            String userId = "user-1";
            CreateOrderItem itemInput = new CreateOrderItem(1L, 1);
            CreateOrderRequest request = new CreateOrderRequest(List.of(itemInput), "Av. Central", "Lima", "999888777");

            when(catalogClient.validateStock(any())).thenReturn(new ValidateStockResponse(true, List.of(new ItemsValidateStock(1L, true, 10))));
            when(catalogClient.getProductById(1L)).thenReturn(new GetProduct(1L, "Mouse", "A great mouse", new BigDecimal("100.00"), 10, "http://image.url", "Electronics", true));
            when(stripeService.createPaymentIntent(anyLong())).thenThrow(new RuntimeException("Stripe API error"));

            ResponseStatusException exception = assertThrows(
                    ResponseStatusException.class,
                    () -> orderService.createOrder(userId, request)
            );

            assertEquals(HttpStatus.PAYMENT_REQUIRED, exception.getStatusCode());
        }
    }

    @Nested
    @DisplayName("Pruebas para completeOrderPayment")
    class CompleteOrderPaymentTests {

        @Test
        @DisplayName("Debe marcar la orden como PAID y descontar stock")
        void completeOrderPayment_Success() {
            String paymentIntentId = "pi_mock_123";
            OrderEntity order = new OrderEntity();
            order.setId("ord-123");
            order.setStatus("PENDING_PAYMENT");

            List<CreateOrderItem> items = List.of(new CreateOrderItem(1L, 2));

            when(orderRepository.findByStripePaymentIntentId(paymentIntentId)).thenReturn(Optional.of(order));
            when(orderItemsRepository.findByOrderId("ord-123")).thenReturn(items);

            orderService.completeOrderPayment(paymentIntentId);

            assertEquals("PAID", order.getStatus());
            verify(orderRepository).save(order);
            verify(catalogClient).reduceStock(1L, 2);
        }

        @Test
        @DisplayName("Idempotencia: Si la orden ya es PAID, no realiza acciones adicionales")
        void completeOrderPayment_AlreadyPaid_ShouldDoNothing() {
            String paymentIntentId = "pi_mock_123";
            OrderEntity order = new OrderEntity();
            order.setId("ord-123");
            order.setStatus("PAID");

            when(orderRepository.findByStripePaymentIntentId(paymentIntentId)).thenReturn(Optional.of(order));

            orderService.completeOrderPayment(paymentIntentId);

            verify(orderRepository, never()).save(any());
            verifyNoInteractions(catalogClient);
        }

        @Test
        @DisplayName("Debe lanzar NOT_FOUND (404) si el PaymentIntent ID no existe")
        void completeOrderPayment_NotFound() {
            String paymentIntentId = "pi_invalid";
            when(orderRepository.findByStripePaymentIntentId(paymentIntentId)).thenReturn(Optional.empty());

            ResponseStatusException exception = assertThrows(
                    ResponseStatusException.class,
                    () -> orderService.completeOrderPayment(paymentIntentId)
            );

            assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        }
    }

    @Test
    @DisplayName("Debe actualizar el estado de la orden")
    void updateOrderStatus_Success() {
        orderService.updateOrderStatus("ord-1", "DELIVERED");
        verify(orderRepository).updateOrderStatus("ord-1", "DELIVERED");
    }
}