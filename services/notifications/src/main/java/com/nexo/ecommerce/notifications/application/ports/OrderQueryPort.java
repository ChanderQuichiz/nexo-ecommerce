package com.nexo.ecommerce.notifications.application.ports;

import com.nexo.ecommerce.notifications.application.usecases.dto.OrderResponse;

public interface OrderQueryPort {

    OrderResponse getOrderById(String orderId);
}
