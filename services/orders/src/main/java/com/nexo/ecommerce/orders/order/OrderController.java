package com.nexo.ecommerce.orders.order;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nexo.ecommerce.orders.order.dto.CreateOrderRequest;
import com.nexo.ecommerce.orders.order.dto.CreateOrderResponse;
import com.nexo.ecommerce.orders.order.dto.GetOrderResponse;
import com.nexo.ecommerce.orders.order.dto.UpdateOrderStatusRequest;

@RestController 
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetOrderResponse> getOrderbyId(@PathVariable String id) {
        GetOrderResponse order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }


    @GetMapping
    public ResponseEntity<List<GetOrderResponse>> getAllOrders() {
        List<GetOrderResponse> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @PatchMapping("/{id}/status")
   public ResponseEntity<Void> updateOrderStatus(@PathVariable  String id, @RequestBody UpdateOrderStatusRequest request) {
        orderService.updateOrderStatus(id, request.status());
        return ResponseEntity.noContent().build();
    }


    @GetMapping("me")
    public ResponseEntity<List<GetOrderResponse>> getOrdersByUserId(@RequestHeader("X-User-ID") String userId) {
        List<GetOrderResponse> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }


    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestHeader("X-User-ID") String userId, @RequestBody CreateOrderRequest request) {
        CreateOrderResponse response = orderService.createOrder(userId, request);
        return ResponseEntity.ok(response);
    }

}
