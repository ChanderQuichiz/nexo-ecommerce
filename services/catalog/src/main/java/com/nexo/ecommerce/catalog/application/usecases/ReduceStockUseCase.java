package com.nexo.ecommerce.catalog.application.usecases;

import com.nexo.ecommerce.catalog.application.ports.TransactionManager;
import com.nexo.ecommerce.catalog.application.repositories.ProductRepository;
import com.nexo.ecommerce.catalog.domain.entities.Product;
import com.nexo.ecommerce.catalog.domain.exceptions.ProductNotFoundException;

public class ReduceStockUseCase {

    private final ProductRepository productRepository;
    private final TransactionManager transactionManager;

    public ReduceStockUseCase(
            ProductRepository productRepository,
            TransactionManager transactionManager
    ) {
        this.productRepository = productRepository;
        this.transactionManager = transactionManager;
    }

    public void execute(
            String productId,
            Integer quantity
    ) {
        transactionManager.executeInTransaction(() -> {
            Product product = productRepository
                    .findByIdForStockUpdate(productId)
                    .orElseThrow(
                            () -> new ProductNotFoundException(
                                    productId
                            )
                    );

            product.reduceStock(quantity);
            productRepository.save(product);

            return null;
        });
    }
}