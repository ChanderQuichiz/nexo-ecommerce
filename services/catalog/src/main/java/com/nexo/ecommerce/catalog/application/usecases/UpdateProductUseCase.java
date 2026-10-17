package com.nexo.ecommerce.catalog.application.usecases;

import com.nexo.ecommerce.catalog.application.ports.ImageStoragePort;
import com.nexo.ecommerce.catalog.application.ports.ImageStoragePort.StoredImage;
import com.nexo.ecommerce.catalog.application.repositories.ProductRepository;
import com.nexo.ecommerce.catalog.application.usecases.dto.ProductImage;
import com.nexo.ecommerce.catalog.application.usecases.dto.ProductResponse;
import com.nexo.ecommerce.catalog.application.usecases.dto.UpdateProductRequest;
import com.nexo.ecommerce.catalog.domain.entities.Product;
import com.nexo.ecommerce.catalog.domain.exceptions.ProductAlreadyExistsException;
import com.nexo.ecommerce.catalog.domain.exceptions.ProductNotFoundException;

public class UpdateProductUseCase {

    private final ProductRepository productRepository;
    private final ImageStoragePort imageStoragePort;

    public UpdateProductUseCase(
            ProductRepository productRepository,
            ImageStoragePort imageStoragePort
    ) {
        this.productRepository = productRepository;
        this.imageStoragePort = imageStoragePort;
    }

    public ProductResponse execute(
            String productId,
            UpdateProductRequest request,
            ProductImage newImage
    ) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "Product request is required"
            );
        }

        Product product = productRepository
                .findById(productId)
                .orElseThrow(
                        () -> new ProductNotFoundException(productId)
                );

        validateUniqueName(productId, request.name());

        product.updateDetails(
                request.name(),
                request.description(),
                request.price(),
                request.category()
        );

        product.updateStock(request.stock());

        if (newImage == null) {
            return ProductResponse.fromDomain(
                    productRepository.save(product)
            );
        }

        StoredImage storedImage = imageStoragePort.upload(
                newImage.content(),
                newImage.contentType(),
                newImage.originalFilename()
        );

        try {
            product.updateImage(storedImage.url());

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

    private void validateUniqueName(
            String productId,
            String name
    ) {
        if (name != null
                && productRepository
                .existsByNameIgnoreCaseAndIdNot(
                        name.trim(),
                        productId
                )) {
            throw new ProductAlreadyExistsException(name);
        }
    }
}