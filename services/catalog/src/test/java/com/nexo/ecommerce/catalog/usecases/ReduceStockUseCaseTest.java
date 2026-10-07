package com.nexo.ecommerce.catalog.usecases;

import com.nexo.ecommerce.catalog.application.ports.TransactionManager;
import com.nexo.ecommerce.catalog.application.repositories.ProductRepository;
import com.nexo.ecommerce.catalog.application.usecases.ReduceStockUseCase;
import com.nexo.ecommerce.catalog.domain.entities.Product;
import com.nexo.ecommerce.catalog.domain.exceptions.ProductNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReduceStockUseCaseTest {

    private static final String PRODUCT_ID =
            "21fcc871-78ef-442b-a29c-ba74620cdb43";

    @Mock
    private ProductRepository productRepository;

    @Mock
    private TransactionManager transactionManager;

    private ReduceStockUseCase reduceStockUseCase;

    @BeforeEach
    void setUp() {
        reduceStockUseCase = new ReduceStockUseCase(
                productRepository,
                transactionManager
        );

        when(transactionManager.executeInTransaction(any()))
                .thenAnswer(invocation -> {
                    Supplier<?> operation =
                            invocation.getArgument(0);

                    return operation.get();
                });
    }

    @Test
    void shouldReduceStockInsideTransaction() {
        Product product = createProduct(10);

        when(productRepository.findByIdForStockUpdate(
                PRODUCT_ID
        )).thenReturn(Optional.of(product));

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        reduceStockUseCase.execute(PRODUCT_ID, 2);

        ArgumentCaptor<Product> productCaptor =
                ArgumentCaptor.forClass(Product.class);

        verify(productRepository)
                .save(productCaptor.capture());

        Product savedProduct =
                productCaptor.getValue();

        assertEquals(8, savedProduct.getStock());

        verify(transactionManager)
                .executeInTransaction(any());

        verify(productRepository)
                .findByIdForStockUpdate(PRODUCT_ID);
    }

    @Test
    void shouldRejectReductionWhenStockIsInsufficient() {
        Product product = createProduct(5);

        when(productRepository.findByIdForStockUpdate(
                PRODUCT_ID
        )).thenReturn(Optional.of(product));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> reduceStockUseCase.execute(
                        PRODUCT_ID,
                        10
                )
        );

        assertEquals(
                "Stock insuficiente.",
                exception.getMessage()
        );

        assertEquals(5, product.getStock());

        verify(productRepository, never())
                .save(any(Product.class));
    }

    @Test
    void shouldThrowExceptionWhenProductDoesNotExist() {
        when(productRepository.findByIdForStockUpdate(
                PRODUCT_ID
        )).thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> reduceStockUseCase.execute(
                        PRODUCT_ID,
                        2
                )
        );

        verify(productRepository, never())
                .save(any(Product.class));
    }

    private Product createProduct(Integer stock) {
        return Product.restore(
                PRODUCT_ID,
                "Laptop Lenovo IdeaPad",
                "Laptop con 16 GB de RAM",
                new BigDecimal("2499.90"),
                stock,
                "https://cdn.test/laptop.jpg",
                "Tecnología",
                true
        );
    }
}