package com.nexo.ecommerce.catalog.infrastructure.persistence;

import com.nexo.ecommerce.catalog.application.repositories.ProductRepository;
import com.nexo.ecommerce.catalog.domain.entities.Product;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepositoryPgImpl
        implements ProductRepository {

    private final ProductRepositoryJpa productRepositoryJpa;

    public ProductRepositoryPgImpl(
            ProductRepositoryJpa productRepositoryJpa
    ) {
        this.productRepositoryJpa = productRepositoryJpa;
    }

    @Override
    public Product save(Product product) {
        ProductEntityJpa entity =
                ProductMapper.toEntity(product);

        ProductEntityJpa savedEntity =
                productRepositoryJpa.save(entity);

        return ProductMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Product> findById(String productId) {
        return productRepositoryJpa.findById(productId)
                .map(ProductMapper::toDomain);
    }

    @Override
    public Optional<Product> findByIdForStockUpdate(
            String productId
    ) {
        return productRepositoryJpa
                .findByIdForStockUpdate(productId)
                .map(ProductMapper::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return productRepositoryJpa.findAll()
                .stream()
                .map(ProductMapper::toDomain)
                .toList();
    }

    @Override
    public List<Product> findAllActive() {
        return productRepositoryJpa
                .findAllByActiveTrueOrderByNameAsc()
                .stream()
                .map(ProductMapper::toDomain)
                .toList();
    }

    @Override
    public List<Product> searchActive(
            String search,
            String category
    ) {
        String normalizedSearch = normalize(search);
        String normalizedCategory = normalize(category);

        return productRepositoryJpa
                .searchActive(
                        normalizedSearch,
                        normalizedCategory
                )
                .stream()
                .map(ProductMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByNameIgnoreCase(String name) {
        return productRepositoryJpa
                .existsByNameIgnoreCase(name);
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}