package com.nexo.ecommerce.catalog.usecases;

import com.nexo.ecommerce.catalog.application.repositories.ProductRepository;
import com.nexo.ecommerce.catalog.application.usecases.ValidateStockUseCase;
import com.nexo.ecommerce.catalog.application.usecases.dto.StockItemRequest;
import com.nexo.ecommerce.catalog.application.usecases.dto.StockItemResponse;
import com.nexo.ecommerce.catalog.application.usecases.dto.ValidateStockRequest;
import com.nexo.ecommerce.catalog.application.usecases.dto.ValidateStockResponse;
import com.nexo.ecommerce.catalog.domain.entities.Product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidateStockUseCaseTest {

    private static final String PRODUCT_ID =
            "21fcc871-78ef-442b-a29c-ba74620cdb43";

    @Mock
    private ProductRepository productRepository;

    private ValidateStockUseCase validateStockUseCase;

    @BeforeEach
    void setUp() {
        validateStockUseCase =
                new ValidateStockUseCase(productRepository);
    }

    @Test
    void shouldReturnAvailableWhenProductHasEnoughStock() {
        Product product = createProduct(10, true);

        when(productRepository.findById(PRODUCT_ID))
                .thenReturn(Optional.of(product));

        ValidateStockRequest request =
                createRequest(2);

        ValidateStockResponse response =
                validateStockUseCase.execute(request);

        assertTrue(response.available());
        assertEquals(1, response.items().size());

        StockItemResponse itemResponse =
                response.items().getFirst();

        assertEquals(
                PRODUCT_ID,
                itemResponse.productId()
        );
        assertTrue(itemResponse.hasStock());
        assertEquals(
                8,
                itemResponse.remainingStock()
        );

        verify(productRepository).findById(PRODUCT_ID);
    }

    @Test
    void shouldReturnUnavailableWhenStockIsInsufficient() {
        Product product = createProduct(5, true);

        when(productRepository.findById(PRODUCT_ID))
                .thenReturn(Optional.of(product));

        ValidateStockResponse response =
                validateStockUseCase.execute(
                        createRequest(10)
                );

        assertFalse(response.available());

        StockItemResponse itemResponse =
                response.items().getFirst();

        assertFalse(itemResponse.hasStock());
        assertEquals(
                5,
                itemResponse.remainingStock()
        );
    }

    @Test
    void shouldReturnUnavailableWhenProductDoesNotExist() {
        when(productRepository.findById(PRODUCT_ID))
                .thenReturn(Optional.empty());

        ValidateStockResponse response =
                validateStockUseCase.execute(
                        createRequest(2)
                );

        assertFalse(response.available());

        StockItemResponse itemResponse =
                response.items().getFirst();

        assertEquals(
                PRODUCT_ID,
                itemResponse.productId()
        );
        assertFalse(itemResponse.hasStock());
        assertEquals(
                0,
                itemResponse.remainingStock()
        );
    }

    @Test
    void shouldRejectRequestWithoutItems() {
        ValidateStockRequest request =
                new ValidateStockRequest(List.of());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> validateStockUseCase.execute(request)
        );

        assertEquals(
                "At least one item is required",
                exception.getMessage()
        );

        verifyNoInteractions(productRepository);
    }

    private ValidateStockRequest createRequest(
            Integer quantity
    ) {
        StockItemRequest item = new StockItemRequest(
                PRODUCT_ID,
                quantity
        );

        return new ValidateStockRequest(
                List.of(item)
        );
    }

    private Product createProduct(
            Integer stock,
            boolean active
    ) {
        return Product.restore(
                PRODUCT_ID,
                "Laptop Lenovo IdeaPad",
                "Laptop con 16 GB de RAM",
                new BigDecimal("2499.90"),
                stock,
                "https://cdn.test/laptop.jpg",
                "Tecnología",
                active
        );
    }
}