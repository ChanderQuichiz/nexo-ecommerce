package com.nexo.ecommerce.catalog.domain.exceptions;

public class ProductAlreadyExistsException
        extends RuntimeException {

    public ProductAlreadyExistsException(String name) {
        super("Ya existe un producto con este nombre: " + name);
    }
}