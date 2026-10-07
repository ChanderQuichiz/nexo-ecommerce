package com.nexo.ecommerce.catalog.application.usecases;

import com.nexo.ecommerce.catalog.application.ports.ImageStoragePort;
import com.nexo.ecommerce.catalog.application.ports.ImageStoragePort.StoredImage;
import com.nexo.ecommerce.catalog.application.repositories.ProductRepository;
import com.nexo.ecommerce.catalog.application.usecases.dto.CreateProductRequest;
import com.nexo.ecommerce.catalog.application.usecases.dto.ProductImage;
import com.nexo.ecommerce.catalog.application.usecases.dto.ProductResponse;
import com.nexo.ecommerce.catalog.domain.entities.Product;
import com.nexo.ecommerce.catalog.domain.exceptions.ProductAlreadyExistsException;

public class CreateProductUseCase {

    private final ProductRepository productRepository;
    private final ImageStoragePort imageStoragePort;

    public CreateProductUseCase(
            ProductRepository productRepository,
            ImageStoragePort imageStoragePort
    ) {
        this.productRepository = productRepository;
        this.imageStoragePort = imageStoragePort;
    }

    public ProductResponse execute(
            CreateProductRequest request,
            ProductImage image
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "Product request is required"
            );
        }

        if (image == null) {
            throw new IllegalArgumentException(
                    "Product image is required"
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

        StoredImage storedImage = imageStoragePort.upload(
                image.content(),
                image.contentType(),
                image.originalFilename()
        );

        try {
            Product product = Product.create(
                    request.name(),
                    request.description(),
                    request.price(),
                    request.stock(),
                    storedImage.url(),
                    request.category()
            );

            Product savedProduct =
                    productRepository.save(product);

            return ProductResponse.fromDomain(savedProduct);

        } catch (RuntimeException exception) {
            try {
                imageStoragePort.delete(storedImage.key());
            } catch (RuntimeException deleteException) {
                exception.addSuppressed(deleteException);
            }

            throw exception;
        }
    }
}