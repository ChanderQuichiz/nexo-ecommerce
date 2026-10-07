package com.nexo.ecommerce.catalog.usecases;

import com.nexo.ecommerce.catalog.application.repositories.ProductRepository;
import com.nexo.ecommerce.catalog.application.usecases.ToggleProductActiveUseCase;
import com.nexo.ecommerce.catalog.domain.entities.Product;
import com.nexo.ecommerce.catalog.domain.exceptions.ProductNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ToggleProductActiveUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    private ToggleProductActiveUseCase
            toggleProductActiveUseCase;

    @BeforeEach
    void setUp() {
        toggleProductActiveUseCase =
                new ToggleProductActiveUseCase(
                        productRepository
                );
    }

    @Test
    void shouldDeactivateActiveProduct() {
        Product product = Product.restore(
                "product-1",
                "Mouse Logitech",
                "Mouse gamer",
                new BigDecimal("129.90"),
                10,
                "https://cdn.test/mouse.jpg",
                "Tecnología",
                true
        );

        when(productRepository.findById("product-1"))
                .thenReturn(Optional.of(product));

        toggleProductActiveUseCase.execute("product-1");

        assertFalse(product.isActive());

        verify(productRepository).save(product);
    }

    @Test
    void shouldActivateInactiveProduct() {
        Product product = Product.restore(
                "product-1",
                "Mouse Logitech",
                "Mouse gamer",
                new BigDecimal("129.90"),
                10,
                "https://cdn.test/mouse.jpg",
                "Tecnología",
                false
        );

        when(productRepository.findById("product-1"))
                .thenReturn(Optional.of(product));

        toggleProductActiveUseCase.execute("product-1");

        assertTrue(product.isActive());

        verify(productRepository).save(product);
    }

    @Test
    void shouldThrowExceptionWhenProductDoesNotExist() {
        when(productRepository.findById("unknown-id"))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> toggleProductActiveUseCase.execute(
                        "unknown-id"
                )
        );

        verify(productRepository, never())
                .save(any(Product.class));
    }
}