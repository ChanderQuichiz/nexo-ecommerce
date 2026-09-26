package com.nexo.ecommerce.orders.application.usecases;
import java.util.List;
import com.nexo.ecommerce.orders.application.repositories.OrderRepository;
import com.nexo.ecommerce.orders.application.usecases.dto.OrderDto;
import com.nexo.ecommerce.orders.application.usecases.dto.OrderMapperDto;
public class OrderReadMeUseCase {
    private final OrderRepository orderRepository;
    public OrderReadMeUseCase(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    public List<OrderDto> execute(String userId) {
        // Implementation for reading all orders
        List<com.nexo.ecommerce.orders.domain.entities.Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
               .map(order -> OrderMapperDto.toDto(order))
                .toList();      
    }
}
