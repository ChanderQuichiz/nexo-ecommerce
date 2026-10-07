package com.nexo.ecommerce.catalog.usecases;

import com.nexo.ecommerce.catalog.application.repositories.ProductRepository;
import com.nexo.ecommerce.catalog.application.usecases.GetProductByIdUseCase;
import com.nexo.ecommerce.catalog.application.usecases.dto.ProductResponse;
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
class GetProductByIdUseCaseTest {

    private static final String PRODUCT_ID =
            "21fcc871-78ef-442b-a29c-ba74620cdb43";

    @Mock
    private ProductRepository productRepository;

    private GetProductByIdUseCase getProductByIdUseCase;

    @BeforeEach
    void setUp() {
        getProductByIdUseCase =
                new GetProductByIdUseCase(productRepository);
    }

    @Test
    void shouldReturnProductWhenProductExists() {
        Product product = Product.restore(
                PRODUCT_ID,
                "Laptop Lenovo IdeaPad",
                "Laptop con 16 GB de RAM",
                new BigDecimal("2499.90"),
                10,
                "https://cdn.test/laptop.jpg",
                "Tecnología",
                true
        );

        when(productRepository.findById(PRODUCT_ID))
                .thenReturn(Optional.of(product));

        ProductResponse response =
                getProductByIdUseCase.execute(PRODUCT_ID);

        assertNotNull(response);
        assertEquals(PRODUCT_ID, response.id());
        assertEquals(
                "Laptop Lenovo IdeaPad",
                response.name()
        );
        assertEquals(
                new BigDecimal("2499.90"),
                response.price()
        );
        assertEquals(10, response.stock());
        assertTrue(response.active());

        verify(productRepository).findById(PRODUCT_ID);
    }

    @Test
    void shouldThrowExceptionWhenProductDoesNotExist() {
        when(productRepository.findById(PRODUCT_ID))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> getProductByIdUseCase.execute(PRODUCT_ID)
        );

        verify(productRepository).findById(PRODUCT_ID);
    }
}