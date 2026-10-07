package com.nexo.ecommerce.catalog.infrastructure.storage.s3;

import com.nexo.ecommerce.catalog.application.ports.ImageStoragePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

@Component
public class S3ImageStorageAdapter implements ImageStoragePort {

    private final S3Client s3Client;
    private final String bucket;
    private final String cdnUrl;

    public S3ImageStorageAdapter(
            S3Client s3Client,
            @Value("${aws.s3.bucket}") String bucket,
            @Value("${aws.s3.cdn-url}") String cdnUrl
    ) {
        this.s3Client = s3Client;
        this.bucket = bucket;
        this.cdnUrl = removeTrailingSlash(cdnUrl);
    }

    @Override
    public StoredImage upload(
            byte[] content,
            String contentType,
            String originalFilename
    ) {
        String key = generateKey(originalFilename);

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        s3Client.putObject(
                request,
                RequestBody.fromBytes(content)
        );

        return new StoredImage(
                key,
                cdnUrl + "/" + key
        );
    }

    @Override
    public void delete(String key) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3Client.deleteObject(request);
    }

    private String generateKey(String originalFilename) {
        return "uploads/products/"
                + UUID.randomUUID()
                + getExtension(originalFilename);
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }

        return filename.substring(filename.lastIndexOf("."))
                .toLowerCase();
    }

    private String removeTrailingSlash(String url) {
        return url.endsWith("/")
                ? url.substring(0, url.length() - 1)
                : url;
    }
}