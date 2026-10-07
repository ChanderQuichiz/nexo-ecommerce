package com.nexo.ecommerce.catalog.application.repositories;

import com.nexo.ecommerce.catalog.domain.entities.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(String productId);

    List<Product> findAll();

    List<Product> findAllActive();

    List<Product> searchActive(
            String search,
            String category
    );

    boolean existsByNameIgnoreCase(String name);

    Optional<Product> findByIdForStockUpdate(String productId);
}