package com.nexo.ecommerce.catalog.application.usecases;

import com.nexo.ecommerce.catalog.application.repositories.ProductRepository;
import com.nexo.ecommerce.catalog.application.usecases.dto.CreateProductRequest;
import com.nexo.ecommerce.catalog.application.usecases.dto.ProductResponse;
import com.nexo.ecommerce.catalog.domain.entities.Product;
import com.nexo.ecommerce.catalog.domain.exceptions.ProductAlreadyExistsException;

public class CreateProductUseCase {

    private final ProductRepository productRepository;

    public CreateProductUseCase(
            ProductRepository productRepository
    ) {
        this.productRepository = productRepository;
    }

    public ProductResponse execute(
            CreateProductRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "Product request is required"
            );
        }

        if (request.name() != null
                && productRepository.existsByNameIgnoreCase(
                request.name().trim()
        )) {
            throw new ProductAlreadyExistsException(
                    request.name()
            );
        }

        Product product = Product.create(
                request.name(),
                request.description(),
                request.price(),
                request.stock(),
                request.imageUrl(),
                request.category()
        );

        Product savedProduct =
                productRepository.save(product);

        return ProductResponse.fromDomain(savedProduct);
    }
}