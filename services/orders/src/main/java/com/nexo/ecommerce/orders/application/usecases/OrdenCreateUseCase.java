package com.nexo.ecommerce.orders.application.usecases;

import com.nexo.ecommerce.orders.application.repositories.OrderRepository;
import com.nexo.ecommerce.orders.application.usecases.dto.OrdenCreateRequestDto;
import com.nexo.ecommerce.orders.application.usecases.dto.OrdenCreateResponseDto;
import com.nexo.ecommerce.orders.domain.entities.Order;
import com.nexo.ecommerce.orders.domain.value_objects.Address;
import com.nexo.ecommerce.orders.domain.value_objects.City;
import com.nexo.ecommerce.orders.domain.value_objects.Item;
import com.nexo.ecommerce.orders.domain.value_objects.Phone;
import com.nexo.ecommerce.orders.domain.value_objects.UserId;

import java.util.List;

import com.nexo.ecommerce.orders.application.client.CatalogClient;
import com.nexo.ecommerce.orders.application.client.dto.ValidateStockRequest;
import com.nexo.ecommerce.orders.application.client.dto.ValidateStockResponse;
public class OrdenCreateUseCase {
    private final OrderRepository orderRepository;
    private final CatalogClient catalogClient;
public OrdenCreateUseCase(OrderRepository orderRepository, CatalogClient catalogClient) {
        // Constructor vacío
        this.orderRepository = orderRepository;
        this.catalogClient = catalogClient;
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
            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("No hay suficiente stock para los siguientes productos: ");
            for (var item : validateStockResponse.items()) {
                if (!item.hasStock()) {
                    messageBuilder.append("Producto ID: ").append(item.productId())
                            .append(", Stock restante: ").append(item.remainingStock()).append("; ");
                }
            }
            throw new IllegalArgumentException(messageBuilder.toString());
        }

       Order saveOrder = Order.create(userId, address, city, phone, items);

        this.orderRepository.save(saveOrder);
        
        for (Item item : saveOrder.getItems()) {
            catalogClient.reduceStock(item.productId(), item.quantity());
        }


        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Orden creada exitosamente con ID: ").append(saveOrder.getId());
        return new OrdenCreateResponseDto(messageBuilder.toString());

    }


}
