package com.nexo.ecommerce.catalog.infrastructure.persistence;

import com.nexo.ecommerce.catalog.domain.entities.Product;

public final class ProductMapper {

    private ProductMapper() {
    }

    public static ProductEntityJpa toEntity(Product product) {
        ProductEntityJpa entity = new ProductEntityJpa();

        entity.setId(product.getId());
        entity.setName(product.getName());
        entity.setDescription(product.getDescription());
        entity.setPrice(product.getPrice());
        entity.setStock(product.getStock());
        entity.setImageUrl(product.getImageUrl());
        entity.setCategory(product.getCategory());
        entity.setActive(product.isActive());

        return entity;
    }

    public static Product toDomain(ProductEntityJpa entity) {
        return Product.restore(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getStock(),
                entity.getImageUrl(),
                entity.getCategory(),
                entity.getActive()
        );
    }
}