package com.nexo.ecommerce.catalog.presentation.controllers;

import com.nexo.ecommerce.catalog.application.usecases.GetProductByIdUseCase;
import com.nexo.ecommerce.catalog.application.usecases.ReduceStockUseCase;
import com.nexo.ecommerce.catalog.application.usecases.ValidateStockUseCase;
import com.nexo.ecommerce.catalog.application.usecases.dto.ProductResponse;
import com.nexo.ecommerce.catalog.application.usecases.dto.ValidateStockRequest;
import com.nexo.ecommerce.catalog.application.usecases.dto.ValidateStockResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/catalog")
public class CatalogController {

    private final GetProductByIdUseCase getProductByIdUseCase;
    private final ValidateStockUseCase validateStockUseCase;
    private final ReduceStockUseCase reduceStockUseCase;

    public CatalogController(
            GetProductByIdUseCase getProductByIdUseCase,
            ValidateStockUseCase validateStockUseCase,
            ReduceStockUseCase reduceStockUseCase
    ) {
        this.getProductByIdUseCase = getProductByIdUseCase;
        this.validateStockUseCase = validateStockUseCase;
        this.reduceStockUseCase = reduceStockUseCase;
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
            @RequestBody Integer quantity
    ) {
        reduceStockUseCase.execute(id, quantity);
        return ResponseEntity.ok().build();
    }
}