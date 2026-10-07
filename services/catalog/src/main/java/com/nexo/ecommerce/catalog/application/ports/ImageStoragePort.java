package com.nexo.ecommerce.catalog.application.ports;

public interface ImageStoragePort {

    StoredImage upload(
            byte[] content,
            String contentType,
            String originalFilename
    );

    void delete(String key);

    record StoredImage(
            String key,
            String url
    ) {
    }
}