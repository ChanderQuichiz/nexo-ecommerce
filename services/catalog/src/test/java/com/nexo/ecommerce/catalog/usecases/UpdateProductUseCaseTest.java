package com.nexo.ecommerce.catalog.usecases;

import com.nexo.ecommerce.catalog.application.ports.ImageStoragePort;
import com.nexo.ecommerce.catalog.application.repositories.ProductRepository;
import com.nexo.ecommerce.catalog.application.usecases.UpdateProductUseCase;
import com.nexo.ecommerce.catalog.application.usecases.dto.ProductImage;
import com.nexo.ecommerce.catalog.application.usecases.dto.ProductResponse;
import com.nexo.ecommerce.catalog.application.usecases.dto.UpdateProductRequest;
import com.nexo.ecommerce.catalog.domain.entities.Product;
import com.nexo.ecommerce.catalog.domain.exceptions.ProductAlreadyExistsException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ImageStoragePort imageStoragePort;

    private UpdateProductUseCase updateProductUseCase;

    private Product existingProduct;

    @BeforeEach
    void setUp() {
        updateProductUseCase = new UpdateProductUseCase(
                productRepository,
                imageStoragePort
        );

        existingProduct = Product.restore(
                "product-1",
                "Mouse Logitech",
                "Mouse original",
                new BigDecimal("100.00"),
                10,
                "https://cdn.test/old-image.jpg",
                "Tecnología",
                true
        );
    }

    @Test
    void shouldUpdateProductWithoutChangingImage() {
        UpdateProductRequest request =
                new UpdateProductRequest(
                        "Mouse Logitech G203",
                        "Mouse gamer actualizado",
                        new BigDecimal("129.90"),
                        20,
                        "Accesorios"
                );

        when(productRepository.findById("product-1"))
                .thenReturn(Optional.of(existingProduct));

        when(productRepository
                .existsByNameIgnoreCaseAndIdNot(
                        "Mouse Logitech G203",
                        "product-1"
                ))
                .thenReturn(false);

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        ProductResponse response =
                updateProductUseCase.execute(
                        "product-1",
                        request,
                        null
                );

        assertEquals(
                "Mouse Logitech G203",
                response.name()
        );
        assertEquals(
                new BigDecimal("129.90"),
                response.price()
        );
        assertEquals(20, response.stock());
        assertEquals("Accesorios", response.category());
        assertEquals(
                "https://cdn.test/old-image.jpg",
                response.imageUrl()
        );

        verify(imageStoragePort, never())
                .upload(any(), any(), any());

        verify(productRepository).save(existingProduct);
    }

    @Test
    void shouldUpdateProductWithNewImage() {
        UpdateProductRequest request =
                new UpdateProductRequest(
                        "Mouse Logitech G203",
                        "Mouse gamer actualizado",
                        new BigDecimal("139.90"),
                        15,
                        "Tecnología"
                );

        ProductImage newImage = new ProductImage(
                "new-image".getBytes(),
                "image/jpeg",
                "mouse-new.jpg"
        );

        when(productRepository.findById("product-1"))
                .thenReturn(Optional.of(existingProduct));

        when(productRepository
                .existsByNameIgnoreCaseAndIdNot(
                        "Mouse Logitech G203",
                        "product-1"
                ))
                .thenReturn(false);

        when(imageStoragePort.upload(
                newImage.content(),
                newImage.contentType(),
                newImage.originalFilename()
        )).thenReturn(
                new ImageStoragePort.StoredImage(
                        "uploads/products/new-image.jpg",
                        "https://cdn.test/"
                                + "uploads/products/new-image.jpg"
                )
        );

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        ProductResponse response =
                updateProductUseCase.execute(
                        "product-1",
                        request,
                        newImage
                );

        assertEquals(
                "https://cdn.test/"
                        + "uploads/products/new-image.jpg",
                response.imageUrl()
        );

        verify(imageStoragePort).upload(
                newImage.content(),
                "image/jpeg",
                "mouse-new.jpg"
        );

        verify(productRepository).save(existingProduct);
    }

    @Test
    void shouldRejectDuplicatedProductName() {
        UpdateProductRequest request =
                new UpdateProductRequest(
                        "Existing product",
                        "Description",
                        new BigDecimal("100.00"),
                        10,
                        "Tecnología"
                );

        when(productRepository.findById("product-1"))
                .thenReturn(Optional.of(existingProduct));

        when(productRepository
                .existsByNameIgnoreCaseAndIdNot(
                        "Existing product",
                        "product-1"
                ))
                .thenReturn(true);

        assertThrows(
                ProductAlreadyExistsException.class,
                () -> updateProductUseCase.execute(
                        "product-1",
                        request,
                        null
                )
        );

        verify(productRepository, never())
                .save(any(Product.class));

        verify(imageStoragePort, never())
                .upload(any(), any(), any());
    }
}