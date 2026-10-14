package com.nexo.ecommerce.catalog.presentation.controllers;

import com.nexo.ecommerce.catalog.application.usecases.*;
import com.nexo.ecommerce.catalog.application.usecases.dto.*;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;



@RestController
@RequestMapping("/catalog")
public class CatalogController {

    private final GetProductByIdUseCase getProductByIdUseCase;
    private final ValidateStockUseCase validateStockUseCase;
    private final ReduceStockUseCase reduceStockUseCase;
    private final CreateProductUseCase createProductUseCase;
    private final ListActiveProductsUseCase listActiveProductsUseCase;
    private final ListAllProductsUseCase listAllProductsUseCase;
    private final SearchProductsUseCase searchProductsUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final ToggleProductActiveUseCase toggleProductActiveUseCase;

    public CatalogController(
            GetProductByIdUseCase getProductByIdUseCase,
            ValidateStockUseCase validateStockUseCase,
            ReduceStockUseCase reduceStockUseCase,
            CreateProductUseCase createProductUseCase,
            ListActiveProductsUseCase listActiveProductsUseCase,
            ListAllProductsUseCase listAllProductsUseCase,
            SearchProductsUseCase searchProductsUseCase,
            UpdateProductUseCase updateProductUseCase,
            ToggleProductActiveUseCase toggleProductActiveUseCase
    ) {
        this.getProductByIdUseCase = getProductByIdUseCase;
        this.validateStockUseCase = validateStockUseCase;
        this.reduceStockUseCase = reduceStockUseCase;
        this.createProductUseCase = createProductUseCase;
        this.listActiveProductsUseCase = listActiveProductsUseCase;
        this.listAllProductsUseCase = listAllProductsUseCase;
        this.searchProductsUseCase = searchProductsUseCase;
        this.updateProductUseCase = updateProductUseCase;
        this.toggleProductActiveUseCase = toggleProductActiveUseCase;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(
            @PathVariable String id
    ) {
        return ResponseEntity.ok(
                getProductByIdUseCase.execute(id)
        );
    }

    @PostMapping("/validate-stock")
    public ResponseEntity<ValidateStockResponse> validateStock(
            @RequestBody ValidateStockRequest request
    ) {
        return ResponseEntity.ok(
                validateStockUseCase.execute(request)
        );
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<Void> reduceStock(
            @PathVariable String id,
            @Valid @RequestBody ReduceStockRequest request
    ) {
        reduceStockUseCase.execute(
                id,
                request.quantity()
        );

        return ResponseEntity.ok().build();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> createProduct(
            @RequestPart("product") CreateProductRequest request,
            @RequestPart("image") MultipartFile image
    ) {
        try {
            ProductImage productImage = new ProductImage(
                    image.getBytes(),
                    image.getContentType(),
                    image.getOriginalFilename()
            );

            ProductResponse response =
                    createProductUseCase.execute(
                            request,
                            productImage
                    );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);

        } catch (IOException exception) {
            throw new IllegalArgumentException(
                    "No se pudo leer la imagen enviada",
                    exception
            );
        }
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category
    ) {
        boolean hasFilters =
                (search != null && !search.isBlank())
                        || (category != null && !category.isBlank());

        if (hasFilters) {
            return ResponseEntity.ok(
                    searchProductsUseCase.execute(
                            search,
                            category
                    )
            );
        }

        return ResponseEntity.ok(
                listActiveProductsUseCase.execute()
        );
    }

    @GetMapping("/admin")
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(
                listAllProductsUseCase.execute()
        );
    }

    @PutMapping(
            value = "/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable String id,
            @RequestPart("product") UpdateProductRequest request,
            @RequestPart(
                    value = "image",
                    required = false
            ) MultipartFile image
    ) {
        try {
            ProductImage productImage = null;

            if (image != null && !image.isEmpty()) {
                productImage = new ProductImage(
                        image.getBytes(),
                        image.getContentType(),
                        image.getOriginalFilename()
                );
            }

            ProductResponse response =
                    updateProductUseCase.execute(
                            id,
                            request,
                            productImage
                    );

            return ResponseEntity.ok(response);

        } catch (IOException exception) {
            throw new IllegalArgumentException(
                    "No se pudo leer la imagen enviada",
                    exception
            );
        }
    }

    @PatchMapping("/{id}/active")
    public ResponseEntity<Void> toggleProductActive(
            @PathVariable String id
    ) {
        toggleProductActiveUseCase.execute(id);

        return ResponseEntity.noContent().build();
    }
}