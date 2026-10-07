package com.nexo.ecommerce.catalog.usecases;

import com.nexo.ecommerce.catalog.application.repositories.ProductRepository;
import com.nexo.ecommerce.catalog.application.usecases.ListActiveProductsUseCase;
import com.nexo.ecommerce.catalog.application.usecases.dto.ProductResponse;
import com.nexo.ecommerce.catalog.domain.entities.Product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListActiveProductsUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    private ListActiveProductsUseCase listActiveProductsUseCase;

    @BeforeEach
    void setUp() {
        listActiveProductsUseCase =
                new ListActiveProductsUseCase(
                        productRepository
                );
    }

    @Test
    void shouldReturnOnlyActiveProducts() {
        Product laptop = createProduct(
                "21fcc871-78ef-442b-a29c-ba74620cdb43",
                "Laptop Lenovo IdeaPad",
                new BigDecimal("2499.90")
        );

        Product mouse = createProduct(
                "d8a84d03-00e5-4dde-8f97-967510c1da61",
                "Mouse Logitech G203",
                new BigDecimal("129.90")
        );

        when(productRepository.findAllActive())
                .thenReturn(List.of(laptop, mouse));

        List<ProductResponse> response =
                listActiveProductsUseCase.execute();

        assertNotNull(response);
        assertEquals(2, response.size());

        assertEquals(
                "Laptop Lenovo IdeaPad",
                response.get(0).name()
        );

        assertEquals(
                "Mouse Logitech G203",
                response.get(1).name()
        );

        assertTrue(response.get(0).active());
        assertTrue(response.get(1).active());

        verify(productRepository).findAllActive();
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoActiveProducts() {
        when(productRepository.findAllActive())
                .thenReturn(List.of());

        List<ProductResponse> response =
                listActiveProductsUseCase.execute();

        assertNotNull(response);
        assertTrue(response.isEmpty());

        verify(productRepository).findAllActive();
    }

    private Product createProduct(
            String id,
            String name,
            BigDecimal price
    ) {
        return Product.restore(
                id,
                name,
                "Descripción del producto",
                price,
                10,
                "https://cdn.test/product.jpg",
                "Tecnología",
                true
        );
    }
}