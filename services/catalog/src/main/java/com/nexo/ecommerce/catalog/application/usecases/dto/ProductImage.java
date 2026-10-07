package com.nexo.ecommerce.catalog.application.usecases.dto;

public record ProductImage(
        byte[] content,
        String contentType,
        String originalFilename
) {

    public ProductImage {
        if (content == null || content.length == 0) {
            throw new IllegalArgumentException(
                    "La imagen del producto es obligatoria"
            );
        }

        if (contentType == null
                || (!contentType.equals("image/jpeg")
                && !contentType.equals("image/png")
                && !contentType.equals("image/webp"))) {
            throw new IllegalArgumentException(
                    "Solo se permiten imágenes JPEG, PNG o WEBP"
            );
        }

        if (content.length > 5 * 1024 * 1024) {
            throw new IllegalArgumentException(
                    "La imagen no puede superar los 5 MB"
            );
        }
    }
}