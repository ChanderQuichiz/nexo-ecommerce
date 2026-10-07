package com.nexo.ecommerce.catalog.domain.entities;

import com.nexo.ecommerce.catalog.domain.value_objects.Price;
import com.nexo.ecommerce.catalog.domain.value_objects.ProductId;
import com.nexo.ecommerce.catalog.domain.value_objects.Stock;

import java.math.BigDecimal;

public class Product {

    private final ProductId id;
    private String name;
    private String description;
    private Price price;
    private Stock stock;
    private String imageUrl;
    private String category;
    private boolean active;

    private Product(
            ProductId id,
            String name,
            String description,
            Price price,
            Stock stock,
            String imageUrl,
            String category,
            boolean active
    ) {
        this.id = id;
        this.name = validateName(name);
        this.description = normalizeDescription(description);
        this.price = price;
        this.stock = stock;
        this.imageUrl = validateImageUrl(imageUrl);
        this.category = validateCategory(category);
        this.active = active;
    }

    public static Product create(
            String name,
            String description,
            BigDecimal price,
            Integer stock,
            String imageUrl,
            String category
    ) {
        return new Product(
                ProductId.generate(),
                name,
                description,
                new Price(price),
                new Stock(stock),
                imageUrl,
                category,
                true
        );
    }

    public static Product restore(
            String id,
            String name,
            String description,
            BigDecimal price,
            Integer stock,
            String imageUrl,
            String category,
            boolean active
    ) {
        return new Product(
                new ProductId(id),
                name,
                description,
                new Price(price),
                new Stock(stock),
                imageUrl,
                category,
                active
        );
    }

    public void updateDetails(
            String name,
            String description,
            BigDecimal price,
            String category
    ) {
        this.name = validateName(name);
        this.description = normalizeDescription(description);
        this.price = new Price(price);
        this.category = validateCategory(category);
    }

    public void updateImage(String imageUrl) {
        this.imageUrl = validateImageUrl(imageUrl);
    }

    public void updateStock(Integer stock) {
        this.stock = new Stock(stock);
    }

    public void reduceStock(Integer quantity) {
        this.stock = this.stock.reduce(quantity);
    }

    public boolean hasStock(Integer quantity) {
        return active && stock.isAvailable(quantity);
    }

    public void toggleActive() {
        this.active = !this.active;
    }

    private static String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Product name is required"
            );
        }

        String normalizedName = name.trim();

        if (normalizedName.length() > 150) {
            throw new IllegalArgumentException(
                    "Product name cannot exceed 150 characters"
            );
        }

        return normalizedName;
    }

    private static String normalizeDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }

        String normalizedDescription = description.trim();

        if (normalizedDescription.length() > 2000) {
            throw new IllegalArgumentException(
                    "Description cannot exceed 2000 characters"
            );
        }

        return normalizedDescription;
    }

    private static String validateImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new IllegalArgumentException(
                    "Image URL is required"
            );
        }

        return imageUrl.trim();
    }

    private static String validateCategory(String category) {
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException(
                    "Category is required"
            );
        }

        String normalizedCategory = category.trim();

        if (normalizedCategory.length() > 100) {
            throw new IllegalArgumentException(
                    "Category cannot exceed 100 characters"
            );
        }

        return normalizedCategory;
    }

    public String getId() {
        return id.value();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price.value();
    }

    public Integer getStock() {
        return stock.value();
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public boolean isActive() {
        return active;
    }
}