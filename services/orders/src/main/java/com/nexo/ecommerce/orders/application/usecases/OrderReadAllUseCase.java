package com.nexo.ecommerce.orders.application.usecases;

import java.util.List;

import com.nexo.ecommerce.orders.application.repositories.OrderRepository;
import com.nexo.ecommerce.orders.application.usecases.dto.OrderDto;
import com.nexo.ecommerce.orders.application.usecases.dto.OrderMapperDto;

public class OrderReadAllUseCase {
private final OrderRepository orderRepository;
    public OrderReadAllUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<OrderDto> execute() {
        // Implementation for reading all orders
        List<com.nexo.ecommerce.orders.domain.entities.Order> orders = orderRepository.findAll();
        return orders.stream()
               .map(order -> OrderMapperDto.toDto(order))
                .toList();      
    }


/*

  String id,
    String userId,
    String address,
    String phone,
    String date,
    String status,
    java.util.List<ItemDto> items,
    BigDecimal subTotal,
    BigDecimal tax,
    BigDecimal total,
    java.util.List<String> paymentIntentId,
    BigDecimal shippingFee,
    String city

*/

}
