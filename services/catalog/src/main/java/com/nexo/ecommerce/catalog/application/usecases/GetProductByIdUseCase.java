package com.nexo.ecommerce.catalog.application.usecases;

import com.nexo.ecommerce.catalog.application.repositories.ProductRepository;
import com.nexo.ecommerce.catalog.application.usecases.dto.ProductResponse;
import com.nexo.ecommerce.catalog.domain.entities.Product;
import com.nexo.ecommerce.catalog.domain.exceptions.ProductNotFoundException;

public class GetProductByIdUseCase {

    private final ProductRepository productRepository;

    public GetProductByIdUseCase(
            ProductRepository productRepository
    ) {
        this.productRepository = productRepository;
    }

    public ProductResponse execute(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(
                        () -> new ProductNotFoundException(productId)
                );

        return ProductResponse.fromDomain(product);
    }
}