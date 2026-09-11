package com.nexo.ecommerce.orders.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.nexo.ecommerce.orders.catalog_client.CatalogClient;
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

import jakarta.transaction.Transactional;

@Service 
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemsRepository orderItemsRepository;
    private final CatalogClient catalogClient;
    private final StripeService stripeService;

    @Value("${constants.interest-rate}")
    private BigDecimal interestRate;
    @Value("${constants.shipping-fee}")
    private BigDecimal shippingFee;

    public OrderService(OrderRepository orderRepository, 
                        OrderItemsRepository orderItemsRepository, 
                        CatalogClient catalogClient, 
                        StripeService stripeService) {
        this.orderRepository = orderRepository;
        this.orderItemsRepository = orderItemsRepository;
        this.catalogClient = catalogClient;
        this.stripeService = stripeService;
    }
   
    public GetOrderResponse getOrderById(String orderId) {
        GetOrderResponse order = orderRepository.findOrderById(orderId);
        if (order == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        }

        List<OrderItem> items = orderItemsRepository.findItemsByOrderId(orderId);
        order.setItems(items);

        return order;
    }

    public List<GetOrderResponse> getAllOrders() {
        List<GetOrderResponse> orders = orderRepository.getAllOrders();
        return mapItemsToOrders(orders);
    }

    public List<GetOrderResponse> getOrdersByUserId(String userId) {
        List<GetOrderResponse> orders = orderRepository.getAllOrdersByUserId(userId);
        return mapItemsToOrders(orders);
    }

    private List<GetOrderResponse> mapItemsToOrders(List<GetOrderResponse> orders) {
        if (orders.isEmpty()) return orders;

        List<String> orderIds = orders.stream().map(GetOrderResponse::getId).collect(Collectors.toList());
        
        // Optimización: Agrupar ítems por ID de orden en un Mapa (O(N + M))
        Map<String, List<OrderItemRow>> itemsByOrder = orderItemsRepository.findItemsByOrderIds(orderIds)
            .stream()
            .collect(Collectors.groupingBy(OrderItemRow::orderId));

        for (GetOrderResponse order : orders) {
            List<OrderItemRow> rows = itemsByOrder.get(order.getId());
            if (rows != null) {
                List<OrderItem> items = rows.stream()
                    .map(row -> new OrderItem(row.productId(), row.name(), row.price(), row.quantity()))
                    .collect(Collectors.toList());
                order.setItems(items);
            }
        }
        return orders;
    }

    public void updateOrderStatus(String orderId, String status) {
        orderRepository.updateOrderStatus(orderId, status);
    }

    @Transactional
    public String createOrder(String userId, CreateOrderRequest request) {
        List<CreateOrderItem> orderItems = request.items();

        // 1. Validar stock
        ValidateStockResponse stockResponse = catalogClient.validateStock(new ValidateStockRequest(orderItems));

        for (ItemsValidateStock item : stockResponse.items()) {
            if (!item.hasStock()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Product with ID " + item.productId() + " is out of stock.");
            }
        }
        
        // 2. Calcular montos
        BigDecimal subtotal = orderItems.stream()
            .map(item -> {
                BigDecimal price = this.catalogClient.getProductById(item.productId()).price();
                return price.multiply(BigDecimal.valueOf(item.quantity()));
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tax = subtotal.multiply(interestRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(tax).add(shippingFee).setScale(2, RoundingMode.HALF_UP);

        long priceInCents = total.multiply(new BigDecimal("100")).longValue();

        // 3. Persistencia inicial
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setUserId(userId);
        orderEntity.setSubtotal(subtotal);
        orderEntity.setShippingFee(shippingFee);
        orderEntity.setTax(tax);
        orderEntity.setTotal(total);
        orderEntity.setStatus("PENDING_PAYMENT");
        orderEntity.setAddress(request.address());
        orderEntity.setCity(request.city());
        orderEntity.setPhone(request.phone());

        orderRepository.save(orderEntity);

        for (CreateOrderItem item : orderItems) {
            OrderItemsEntity orderItemEntity = new OrderItemsEntity();
            orderItemEntity.setOrderId(orderEntity.getId());
            orderItemEntity.setProductId(item.productId());
            orderItemEntity.setQuantity(item.quantity());
            orderItemsRepository.save(orderItemEntity);
        }

        // 4. Integración con Stripe
        try {
            PaymentIntent paymentIntent = this.stripeService.createPaymentIntent(priceInCents);
            orderEntity.setStripePaymentIntentId(paymentIntent.getId());
            orderRepository.save(orderEntity);
            return paymentIntent.getClientSecret();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.PAYMENT_REQUIRED, "Stripe Error: " + e.getMessage());
        }
    }

    @Transactional
    public void completeOrderPayment(String paymentIntentId) {
        OrderEntity order = orderRepository.findByStripePaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        if ("PAID".equals(order.getStatus())) return;

        order.setStatus("PAID");
        orderRepository.save(order);

        List<CreateOrderItem> items = orderItemsRepository.findByOrderId(order.getId());
        for (CreateOrderItem item : items) {
            catalogClient.reduceStock(item.productId(), item.quantity());
        }
    }
}
