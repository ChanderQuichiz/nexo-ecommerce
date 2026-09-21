package com.nexo.ecommerce.orders.application.dto;

import java.math.BigDecimal;
import java.util.List;


public record OrdenCreateRequestDto(
    String userId,
    String address,
    String city,
    String phone,
    List<Item> items
) {

    

    public OrdenCreateRequestDto {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId es obligatorio");
        }
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("city es obligatorio");
        }
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("phone es obligatorio");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Debe tener al menos un item");
        }
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("address es obligatorio");
        }
    }

    public record Item(
        String productId,
        BigDecimal price,
        Integer quantity
    ) {
        public Item {
            if (productId == null || productId.isBlank()) {
                throw new IllegalArgumentException("productId es obligatorio");
            }
            if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("price no puede ser nulo o negativo");
            }
            if (quantity == null || quantity <= 0) {
                throw new IllegalArgumentException("quantity debe ser mayor a cero");
            }
        }
    }
}
