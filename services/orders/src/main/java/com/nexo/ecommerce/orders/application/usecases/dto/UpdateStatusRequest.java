package com.nexo.ecommerce.orders.application.usecases.dto;

public record UpdateStatusRequest(
    String status,
    String orderId
) {
    public UpdateStatusRequest {
        if (status == null || status.isEmpty()) {
            throw new IllegalArgumentException("Status cannot be null or empty");
        }
        if (orderId == null || orderId.isEmpty()) {
            throw new IllegalArgumentException("OrderId cannot be null or empty");
        }
        
    }    

}
