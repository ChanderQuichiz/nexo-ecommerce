package com.nexo.ecommerce.catalog.application.usecases;

import com.nexo.ecommerce.catalog.application.repositories.ProductRepository;
import com.nexo.ecommerce.catalog.application.usecases.dto.ProductResponse;

import java.util.List;

public class SearchProductsUseCase {

    private final ProductRepository productRepository;

    public SearchProductsUseCase(
            ProductRepository productRepository
    ) {
        this.productRepository = productRepository;
    }

    public List<ProductResponse> execute(
            String search,
            String category
    ) {
        return productRepository
                .searchActive(search, category)
                .stream()
                .map(ProductResponse::fromDomain)
                .toList();
    }
}