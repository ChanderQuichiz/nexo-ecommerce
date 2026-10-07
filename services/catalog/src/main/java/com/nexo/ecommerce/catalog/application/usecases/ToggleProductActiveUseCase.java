package com.nexo.ecommerce.catalog.application.usecases;

import com.nexo.ecommerce.catalog.application.repositories.ProductRepository;
import com.nexo.ecommerce.catalog.domain.entities.Product;
import com.nexo.ecommerce.catalog.domain.exceptions.ProductNotFoundException;

public class ToggleProductActiveUseCase {

    private final ProductRepository productRepository;

    public ToggleProductActiveUseCase(
            ProductRepository productRepository
    ) {
        this.productRepository = productRepository;
    }

    public void execute(String productId) {
        Product product = productRepository
                .findById(productId)
                .orElseThrow(
                        () -> new ProductNotFoundException(productId)
                );

        product.toggleActive();

        productRepository.save(product);
    }
}