package com.nexo.ecommerce.catalog.application.usecases;

import com.nexo.ecommerce.catalog.application.repositories.ProductRepository;
import com.nexo.ecommerce.catalog.application.usecases.dto.ProductResponse;

import java.util.List;

public class ListAllProductsUseCase {

    private final ProductRepository productRepository;

    public ListAllProductsUseCase(
            ProductRepository productRepository
    ) {
        this.productRepository = productRepository;
    }

    public List<ProductResponse> execute() {
        return productRepository.findAll()
                .stream()
                .map(ProductResponse::fromDomain)
                .toList();
    }
}