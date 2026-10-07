package com.nexo.ecommerce.catalog.usecases;

import com.nexo.ecommerce.catalog.application.repositories.ProductRepository;
import com.nexo.ecommerce.catalog.application.usecases.ListAllProductsUseCase;
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
class ListAllProductsUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    private ListAllProductsUseCase listAllProductsUseCase;

    @BeforeEach
    void setUp() {
        listAllProductsUseCase =
                new ListAllProductsUseCase(
                        productRepository
                );
    }

    @Test
    void shouldReturnActiveAndInactiveProducts() {
        Product activeProduct = createProduct(
                "21fcc871-78ef-442b-a29c-ba74620cdb43",
                "Laptop Lenovo IdeaPad",
                true
        );

        Product inactiveProduct = createProduct(
                "d8a84d03-00e5-4dde-8f97-967510c1da61",
                "Mouse Logitech G203",
                false
        );

        when(productRepository.findAll())
                .thenReturn(
                        List.of(
                                activeProduct,
                                inactiveProduct
                        )
                );

        List<ProductResponse> response =
                listAllProductsUseCase.execute();

        assertNotNull(response);
        assertEquals(2, response.size());

        assertTrue(response.get(0).active());
        assertFalse(response.get(1).active());

        assertEquals(
                "Laptop Lenovo IdeaPad",
                response.get(0).name()
        );

        assertEquals(
                "Mouse Logitech G203",
                response.get(1).name()
        );

        verify(productRepository).findAll();
        verify(productRepository, never())
                .findAllActive();
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoProducts() {
        when(productRepository.findAll())
                .thenReturn(List.of());

        List<ProductResponse> response =
                listAllProductsUseCase.execute();

        assertNotNull(response);
        assertTrue(response.isEmpty());

        verify(productRepository).findAll();
    }

    private Product createProduct(
            String id,
            String name,
            boolean active
    ) {
        return Product.restore(
                id,
                name,
                "Descripción del producto",
                new BigDecimal("100.00"),
                10,
                "https://cdn.test/product.jpg",
                "Tecnología",
                active
        );
    }
}