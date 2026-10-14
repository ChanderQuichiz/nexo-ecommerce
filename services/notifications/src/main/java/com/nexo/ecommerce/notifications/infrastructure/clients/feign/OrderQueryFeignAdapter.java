package com.nexo.ecommerce.notifications.infrastructure.clients.feign;

import com.nexo.ecommerce.notifications.application.ports.OrderQueryPort;
import com.nexo.ecommerce.notifications.application.exceptions.ExternalServiceUnavailableException;
import com.nexo.ecommerce.notifications.application.exceptions.OrderNotFoundException;
import com.nexo.ecommerce.notifications.application.usecases.dto.OrderResponse;
import feign.FeignException;
import org.springframework.stereotype.Component;

@Component
public class OrderQueryFeignAdapter implements OrderQueryPort {

    private final OrdersFeignClient ordersFeignClient;

    public OrderQueryFeignAdapter(OrdersFeignClient ordersFeignClient) {
        this.ordersFeignClient = ordersFeignClient;
    }

    @Override
    public OrderResponse getOrderById(String orderId) {
        try {
            return ordersFeignClient.getOrderById(orderId);
        } catch (FeignException exception) {
            if (exception.status() == 404) {
                throw new OrderNotFoundException(orderId);
            }
            throw new ExternalServiceUnavailableException("Orders Service", exception);
        }
    }
}
