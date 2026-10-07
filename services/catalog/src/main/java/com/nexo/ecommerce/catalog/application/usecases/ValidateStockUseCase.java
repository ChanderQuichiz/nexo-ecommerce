package com.nexo.ecommerce.catalog.application.usecases;

import com.nexo.ecommerce.catalog.application.repositories.ProductRepository;
import com.nexo.ecommerce.catalog.application.usecases.dto.StockItemRequest;
import com.nexo.ecommerce.catalog.application.usecases.dto.StockItemResponse;
import com.nexo.ecommerce.catalog.application.usecases.dto.ValidateStockRequest;
import com.nexo.ecommerce.catalog.application.usecases.dto.ValidateStockResponse;
import com.nexo.ecommerce.catalog.domain.entities.Product;

import java.util.List;
import java.util.Optional;

public class ValidateStockUseCase {

    private final ProductRepository productRepository;

    public ValidateStockUseCase(
            ProductRepository productRepository
    ) {
        this.productRepository = productRepository;
    }

    public ValidateStockResponse execute(
            ValidateStockRequest request
    ) {
        if (request == null
                || request.items() == null
                || request.items().isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one item is required"
            );
        }

        List<StockItemResponse> results = request.items()
                .stream()
                .map(this::validateItem)
                .toList();

        boolean allAvailable = results.stream()
                .allMatch(StockItemResponse::hasStock);

        return new ValidateStockResponse(
                allAvailable,
                results
        );
    }

    private StockItemResponse validateItem(
            StockItemRequest item
    ) {
        validateRequestItem(item);

        Optional<Product> productOptional =
                productRepository.findById(item.productId());

        if (productOptional.isEmpty()) {
            return new StockItemResponse(
                    item.productId(),
                    false,
                    0
            );
        }

        Product product = productOptional.get();
        boolean hasStock = product.hasStock(item.quantity());

        int remainingStock = hasStock
                ? product.getStock() - item.quantity()
                : product.getStock();

        return new StockItemResponse(
                product.getId(),
                hasStock,
                remainingStock
        );
    }

    private void validateRequestItem(StockItemRequest item) {
        if (item == null) {
            throw new IllegalArgumentException(
                    "Stock item cannot be null"
            );
        }

        if (item.productId() == null
                || item.productId().isBlank()) {
            throw new IllegalArgumentException(
                    "ProductId is required"
            );
        }

        if (item.quantity() == null
                || item.quantity() <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }
    }
}