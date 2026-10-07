package com.nexo.ecommerce.catalog.usecases;

import com.nexo.ecommerce.catalog.application.repositories.ProductRepository;
import com.nexo.ecommerce.catalog.application.usecases.SearchProductsUseCase;
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
class SearchProductsUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    private SearchProductsUseCase searchProductsUseCase;

    @BeforeEach
    void setUp() {
        searchProductsUseCase =
                new SearchProductsUseCase(
                        productRepository
                );
    }

    @Test
    void shouldSearchProductsByText() {
        Product product = createProduct(
                "Laptop Lenovo IdeaPad",
                "Tecnología"
        );

        when(productRepository.searchActive(
                "Lenovo",
                null
        )).thenReturn(List.of(product));

        List<ProductResponse> response =
                searchProductsUseCase.execute(
                        "Lenovo",
                        null
                );

        assertEquals(1, response.size());
        assertEquals(
                "Laptop Lenovo IdeaPad",
                response.getFirst().name()
        );

        verify(productRepository).searchActive(
                "Lenovo",
                null
        );
    }

    @Test
    void shouldSearchProductsByCategory() {
        Product product = createProduct(
                "Mouse Logitech G203",
                "Tecnología"
        );

        when(productRepository.searchActive(
                null,
                "Tecnología"
        )).thenReturn(List.of(product));

        List<ProductResponse> response =
                searchProductsUseCase.execute(
                        null,
                        "Tecnología"
                );

        assertEquals(1, response.size());
        assertEquals(
                "Tecnología",
                response.getFirst().category()
        );

        verify(productRepository).searchActive(
                null,
                "Tecnología"
        );
    }

    @Test
    void shouldReturnEmptyListWhenNoProductsMatch() {
        when(productRepository.searchActive(
                "Producto inexistente",
                "Tecnología"
        )).thenReturn(List.of());

        List<ProductResponse> response =
                searchProductsUseCase.execute(
                        "Producto inexistente",
                        "Tecnología"
                );

        assertNotNull(response);
        assertTrue(response.isEmpty());

        verify(productRepository).searchActive(
                "Producto inexistente",
                "Tecnología"
        );
    }

    private Product createProduct(
            String name,
            String category
    ) {
        return Product.restore(
                "21fcc871-78ef-442b-a29c-ba74620cdb43",
                name,
                "Descripción del producto",
                new BigDecimal("100.00"),
                10,
                "https://cdn.test/product.jpg",
                category,
                true
        );
    }
}