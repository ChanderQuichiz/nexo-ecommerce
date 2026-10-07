package com.nexo.ecommerce.catalog.usecases;

import com.nexo.ecommerce.catalog.application.ports.ImageStoragePort;
import com.nexo.ecommerce.catalog.application.ports.ImageStoragePort.StoredImage;
import com.nexo.ecommerce.catalog.application.repositories.ProductRepository;
import com.nexo.ecommerce.catalog.application.usecases.CreateProductUseCase;
import com.nexo.ecommerce.catalog.application.usecases.dto.CreateProductRequest;
import com.nexo.ecommerce.catalog.application.usecases.dto.ProductImage;
import com.nexo.ecommerce.catalog.application.usecases.dto.ProductResponse;
import com.nexo.ecommerce.catalog.domain.entities.Product;
import com.nexo.ecommerce.catalog.domain.exceptions.ProductAlreadyExistsException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ImageStoragePort imageStoragePort;

    private CreateProductUseCase createProductUseCase;

    private CreateProductRequest request;
    private ProductImage image;

    @BeforeEach
    void setUp() {
        createProductUseCase = new CreateProductUseCase(
                productRepository,
                imageStoragePort
        );

        request = new CreateProductRequest(
                "Mouse Logitech G203",
                "Mouse gamer con iluminación RGB",
                new BigDecimal("129.90"),
                15,
                "Tecnología"
        );

        image = new ProductImage(
                "contenido-imagen".getBytes(
                        StandardCharsets.UTF_8
                ),
                "image/jpeg",
                "mouse.jpg"
        );
    }

    @Test
    void shouldCreateProductWithImageUrlReturnedByStorage() {
        StoredImage storedImage = new StoredImage(
                "uploads/products/test.jpg",
                "https://cdn.test/uploads/products/test.jpg"
        );

        when(productRepository.existsByNameIgnoreCase(
                "Mouse Logitech G203"
        )).thenReturn(false);

        when(imageStoragePort.upload(
                any(byte[].class),
                eq("image/jpeg"),
                eq("mouse.jpg")
        )).thenReturn(storedImage);

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        ProductResponse response =
                createProductUseCase.execute(
                        request,
                        image
                );

        assertNotNull(response);
        assertNotNull(response.id());
        assertEquals(
                "Mouse Logitech G203",
                response.name()
        );
        assertEquals(
                "Mouse gamer con iluminación RGB",
                response.description()
        );
        assertEquals(
                new BigDecimal("129.90"),
                response.price()
        );
        assertEquals(15, response.stock());
        assertEquals(
                "https://cdn.test/uploads/products/test.jpg",
                response.imageUrl()
        );
        assertEquals(
                "Tecnología",
                response.category()
        );
        assertTrue(response.active());

        verify(productRepository)
                .existsByNameIgnoreCase(
                        "Mouse Logitech G203"
                );

        verify(imageStoragePort).upload(
                any(byte[].class),
                eq("image/jpeg"),
                eq("mouse.jpg")
        );

        verify(productRepository)
                .save(any(Product.class));

        verify(imageStoragePort, never())
                .delete(anyString());
    }

    @Test
    void shouldRejectProductWhenNameAlreadyExists() {
        when(productRepository.existsByNameIgnoreCase(
                "Mouse Logitech G203"
        )).thenReturn(true);

        assertThrows(
                ProductAlreadyExistsException.class,
                () -> createProductUseCase.execute(
                        request,
                        image
                )
        );

        verify(imageStoragePort, never()).upload(
                any(byte[].class),
                anyString(),
                anyString()
        );

        verify(productRepository, never())
                .save(any(Product.class));
    }

    @Test
    void shouldDeleteUploadedImageWhenSavingProductFails() {
        StoredImage storedImage = new StoredImage(
                "uploads/products/test.jpg",
                "https://cdn.test/uploads/products/test.jpg"
        );

        when(productRepository.existsByNameIgnoreCase(
                "Mouse Logitech G203"
        )).thenReturn(false);

        when(imageStoragePort.upload(
                any(byte[].class),
                eq("image/jpeg"),
                eq("mouse.jpg")
        )).thenReturn(storedImage);

        when(productRepository.save(any(Product.class)))
                .thenThrow(
                        new RuntimeException("Database error")
                );

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> createProductUseCase.execute(
                        request,
                        image
                )
        );

        assertEquals(
                "Database error",
                exception.getMessage()
        );

        verify(imageStoragePort)
                .delete("uploads/products/test.jpg");
    }
}