package com.nexo.ecommerce.notifications.application.ports;

import com.nexo.ecommerce.notifications.application.usecases.dto.OrderResponse;
import com.nexo.ecommerce.notifications.application.usecases.dto.UserResponse;

public interface ReceiptGeneratorPort {

    byte[] generateReceipt(OrderResponse order, UserResponse user);
}
