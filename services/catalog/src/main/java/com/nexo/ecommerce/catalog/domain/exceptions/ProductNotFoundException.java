package com.nexo.ecommerce.catalog.domain.exceptions;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(String productId) {
        super("Producto no encontrado: " + productId);
    }
}