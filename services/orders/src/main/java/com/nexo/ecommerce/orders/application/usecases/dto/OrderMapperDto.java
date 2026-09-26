package com.nexo.ecommerce.orders.application.usecases.dto;

import com.nexo.ecommerce.orders.application.usecases.dto.OrderDto.ItemDto;
import com.nexo.ecommerce.orders.domain.entities.Order;

public class OrderMapperDto {
    public static OrderDto toDto(Order order) {


return new OrderDto(
            order.getId().value().toString(),
            order.getUserId().value(),
            order.getAddress().value(),
            order.getPhone().value(),
            order.getDate().value(),
            order.getStatus().name(),
            order.getItems().stream()
                .map(item -> new ItemDto(
                    item.productId(),
                    item.quantity(),
                    item.price()
                )).toList(),
            order.getSubtotal().value(),
            order.getTax().value(),
            order.getTotal().value(),
            order.getPaymentIntentId().stream().map(paymentIntentId -> paymentIntentId.value().toString()).toList(),
            order.getShippingFee().value(),
            order.getCity().value()
        );
       
    }



}
