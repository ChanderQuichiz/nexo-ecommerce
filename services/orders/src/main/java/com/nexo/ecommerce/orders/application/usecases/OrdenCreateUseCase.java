package com.nexo.ecommerce.orders.application.usecases;

import com.nexo.ecommerce.orders.application.repositories.OrderRepository;
import com.nexo.ecommerce.orders.application.usecases.dto.OrdenCreateRequestDto;
import com.nexo.ecommerce.orders.application.usecases.dto.OrdenCreateResponseDto;
import com.nexo.ecommerce.orders.domain.entities.Order;
import com.nexo.ecommerce.orders.domain.events.OrderCreatedEvent;
import com.nexo.ecommerce.orders.domain.value_objects.Address;
import com.nexo.ecommerce.orders.domain.value_objects.City;
import com.nexo.ecommerce.orders.domain.value_objects.Item;
import com.nexo.ecommerce.orders.domain.value_objects.Phone;
import com.nexo.ecommerce.orders.domain.value_objects.UserId;

import java.util.List;

import com.nexo.ecommerce.orders.application.client.CatalogClient;
import com.nexo.ecommerce.orders.application.client.dto.ValidateStockRequest;
import com.nexo.ecommerce.orders.application.client.dto.ValidateStockResponse;
import com.nexo.ecommerce.orders.application.ports.EventPublisherPort;
public class OrdenCreateUseCase {
    private final OrderRepository orderRepository;
    private final CatalogClient catalogClient;
    private final EventPublisherPort eventPublisherPort;
public OrdenCreateUseCase(OrderRepository orderRepository, CatalogClient catalogClient, EventPublisherPort eventPublisherPort) {
        // Constructor vacío
        this.orderRepository = orderRepository;
        this.catalogClient = catalogClient;
        this.eventPublisherPort = eventPublisherPort;
    }

    public OrdenCreateResponseDto execute(OrdenCreateRequestDto request) {
        // Lógica para crear la orden

        UserId userId = new UserId(request.userId());
        Address address = new Address(request.address());
        City city = new City(request.city());
        Phone phone = new Phone(request.phone());
        List<Item> items = request.items().stream()
                .map(itemDto -> new Item( itemDto.price(),itemDto.productId(), itemDto.quantity()))
                .toList();

        ValidateStockRequest validateStockRequest = new ValidateStockRequest(items);

        ValidateStockResponse validateStockResponse = catalogClient.validateStock(validateStockRequest);
     
        if (!validateStockResponse.available()) {
            throw new IllegalArgumentException("No hay suficiente stock");
        }

       Order saveOrder = Order.create(userId, address, city, phone, items);

        this.orderRepository.save(saveOrder);
        
        saveOrder.getItems().forEach(item -> {
            catalogClient.reduceStock(item.productId(), item.quantity());
        });
        OrderCreatedEvent eventObject = new OrderCreatedEvent(saveOrder.getId().toString());
        saveOrder.getDomainEvents().forEach((event) -> {
           if (event instanceof OrderCreatedEvent) {
                eventPublisherPort.publishOrderCreatedEvent(eventObject);
            }
        }
        );  

        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Orden creada exitosamente con ID: ").append(saveOrder.getId());
        return new OrdenCreateResponseDto(messageBuilder.toString());

    }


}
