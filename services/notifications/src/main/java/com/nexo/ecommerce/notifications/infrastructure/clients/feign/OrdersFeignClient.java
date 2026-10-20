package com.nexo.ecommerce.notifications.infrastructure.clients.feign;

import com.nexo.ecommerce.notifications.application.usecases.dto.OrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "orders-service",
        fallback = OrdersFeignClientFallback.class)
public interface OrdersFeignClient {

    @GetMapping("/orders/{id}")
    OrderResponse getOrderById(@PathVariable("id") String orderId);
}
