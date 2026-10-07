package com.nexo.ecommerce.catalog.presentation;

import com.nexo.ecommerce.catalog.application.ports.ImageStoragePort;
import com.nexo.ecommerce.catalog.application.repositories.ProductRepository;
import com.nexo.ecommerce.catalog.config.TestcontainersConfiguration;
import com.nexo.ecommerce.catalog.domain.entities.Product;
import com.nexo.ecommerce.catalog.infrastructure.persistence.ProductRepositoryJpa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CatalogControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductRepositoryJpa productRepositoryJpa;

    @MockBean
    private ImageStoragePort imageStoragePort;

    private String productId;

    @BeforeEach
    void setUp() {
        productRepositoryJpa.deleteAll();

        Product product = Product.create(
                "Laptop Lenovo IdeaPad",
                "Laptop con 16 GB de RAM",
                new BigDecimal("2499.90"),
                10,
                "https://cdn.test/laptop.jpg",
                "Tecnología"
        );

        Product savedProduct =
                productRepository.save(product);

        productId = savedProduct.getId();
    }

    @Test
    void shouldListActiveProducts() throws Exception {
        mockMvc.perform(
                        get("/catalog")
                )
                .andExpect(status().isOk())
                .andExpect(
                        content().contentTypeCompatibleWith(
                                MediaType.APPLICATION_JSON
                        )
                )
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id")
                        .value(productId))
                .andExpect(jsonPath("$[0].name")
                        .value("Laptop Lenovo IdeaPad"))
                .andExpect(jsonPath("$[0].stock")
                        .value(10))
                .andExpect(jsonPath("$[0].active")
                        .value(true));
    }

    @Test
    void shouldGetProductById() throws Exception {
        mockMvc.perform(
                        get("/catalog/{id}", productId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(productId))
                .andExpect(jsonPath("$.name")
                        .value("Laptop Lenovo IdeaPad"))
                .andExpect(jsonPath("$.category")
                        .value("Tecnología"))
                .andExpect(jsonPath("$.stock")
                        .value(10));
    }

    @Test
    void shouldReturnNotFoundForUnknownProduct()
            throws Exception {

        String unknownId =
                "00000000-0000-0000-0000-000000000000";

        mockMvc.perform(
                        get("/catalog/{id}", unknownId)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status")
                        .value(404))
                .andExpect(jsonPath("$.error")
                        .value("Not Found"))
                .andExpect(jsonPath("$.message")
                        .value(
                                "Producto no encontrado: "
                                        + unknownId
                        ));
    }

    @Test
    void shouldSearchProductsByText() throws Exception {
        mockMvc.perform(
                        get("/catalog")
                                .param("search", "Lenovo")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()")
                        .value(1))
                .andExpect(jsonPath("$[0].id")
                        .value(productId))
                .andExpect(jsonPath("$[0].name")
                        .value("Laptop Lenovo IdeaPad"));
    }

    @Test
    void shouldValidateStock() throws Exception {
        String requestBody = """
                {
                  "items": [
                    {
                      "productId": "%s",
                      "quantity": 2
                    }
                  ]
                }
                """.formatted(productId);

        mockMvc.perform(
                        post("/catalog/validate-stock")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available")
                        .value(true))
                .andExpect(jsonPath("$.items[0].productId")
                        .value(productId))
                .andExpect(jsonPath("$.items[0].hasStock")
                        .value(true))
                .andExpect(jsonPath(
                        "$.items[0].remainingStock"
                ).value(8));
    }

    @Test
    void shouldReduceStock() throws Exception {
        mockMvc.perform(
                        patch(
                                "/catalog/{id}/stock",
                                productId
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("""
                                        {
                                        "quantity": 2
                                        }
                                """)
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        get("/catalog/{id}", productId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock")
                        .value(8));
    }

    @Test
    void shouldCreateProductWithImage() throws Exception {
        when(imageStoragePort.upload(
                any(byte[].class),
                eq("image/jpeg"),
                eq("mouse.jpg")
        )).thenReturn(
                new ImageStoragePort.StoredImage(
                        "uploads/products/mouse.jpg",
                        "https://cdn.test/uploads/products/mouse.jpg"
                )
        );

        String productJson = """
                {
                  "name": "Mouse Logitech G203",
                  "description": "Mouse gamer RGB",
                  "price": 129.90,
                  "stock": 15,
                  "category": "Tecnología"
                }
                """;

        MockMultipartFile productPart =
                new MockMultipartFile(
                        "product",
                        "",
                        MediaType.APPLICATION_JSON_VALUE,
                        productJson.getBytes(
                                StandardCharsets.UTF_8
                        )
                );

        MockMultipartFile imagePart =
                new MockMultipartFile(
                        "image",
                        "mouse.jpg",
                        MediaType.IMAGE_JPEG_VALUE,
                        "fake-image-content".getBytes(
                                StandardCharsets.UTF_8
                        )
                );

        mockMvc.perform(
                        multipart("/catalog")
                                .file(productPart)
                                .file(imagePart)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name")
                        .value("Mouse Logitech G203"))
                .andExpect(jsonPath("$.stock")
                        .value(15))
                .andExpect(jsonPath("$.imageUrl")
                        .value(
                                "https://cdn.test/"
                                        + "uploads/products/mouse.jpg"
                        ))
                .andExpect(jsonPath("$.category")
                        .value("Tecnología"))
                .andExpect(jsonPath("$.active")
                        .value(true));
    }

    @Test
    void shouldUpdateProductWithoutChangingImage()
            throws Exception {

        String productJson = """
            {
              "name": "Laptop Lenovo Actualizada",
              "description": "Laptop con información actualizada",
              "price": 2999.90,
              "stock": 25,
              "category": "Computadoras"
            }
            """;

        MockMultipartFile productPart =
                new MockMultipartFile(
                        "product",
                        "",
                        MediaType.APPLICATION_JSON_VALUE,
                        productJson.getBytes(
                                StandardCharsets.UTF_8
                        )
                );

        mockMvc.perform(
                        multipart(
                                "/catalog/{id}",
                                productId
                        )
                                .file(productPart)
                                .with(request -> {
                                    request.setMethod("PUT");
                                    return request;
                                })
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(productId))
                .andExpect(jsonPath("$.name")
                        .value("Laptop Lenovo Actualizada"))
                .andExpect(jsonPath("$.price")
                        .value(2999.90))
                .andExpect(jsonPath("$.stock")
                        .value(25))
                .andExpect(jsonPath("$.category")
                        .value("Computadoras"))
                .andExpect(jsonPath("$.imageUrl")
                        .value(
                                "https://cdn.test/laptop.jpg"
                        ));
    }

    @Test
    void shouldUpdateProductWithNewImage()
            throws Exception {

        when(imageStoragePort.upload(
                any(byte[].class),
                eq("image/png"),
                eq("laptop-new.png")
        )).thenReturn(
                new ImageStoragePort.StoredImage(
                        "uploads/products/laptop-new.png",
                        "https://cdn.test/"
                                + "uploads/products/laptop-new.png"
                )
        );

        String productJson = """
            {
              "name": "Laptop Lenovo Nueva",
              "description": "Laptop con imagen actualizada",
              "price": 3199.90,
              "stock": 12,
              "category": "Tecnología"
            }
            """;

        MockMultipartFile productPart =
                new MockMultipartFile(
                        "product",
                        "",
                        MediaType.APPLICATION_JSON_VALUE,
                        productJson.getBytes(
                                StandardCharsets.UTF_8
                        )
                );

        MockMultipartFile imagePart =
                new MockMultipartFile(
                        "image",
                        "laptop-new.png",
                        "image/png",
                        "new-image-content".getBytes(
                                StandardCharsets.UTF_8
                        )
                );

        mockMvc.perform(
                        multipart(
                                "/catalog/{id}",
                                productId
                        )
                                .file(productPart)
                                .file(imagePart)
                                .with(request -> {
                                    request.setMethod("PUT");
                                    return request;
                                })
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(productId))
                .andExpect(jsonPath("$.name")
                        .value("Laptop Lenovo Nueva"))
                .andExpect(jsonPath("$.stock")
                        .value(12))
                .andExpect(jsonPath("$.imageUrl")
                        .value(
                                "https://cdn.test/"
                                        + "uploads/products/"
                                        + "laptop-new.png"
                        ));
    }

    @Test
    void shouldToggleProductActiveStatus()
            throws Exception {

        mockMvc.perform(
                        patch(
                                "/catalog/{id}/active",
                                productId
                        )
                )
                .andExpect(status().isNoContent());

        mockMvc.perform(
                        get(
                                "/catalog/{id}",
                                productId
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active")
                        .value(false));
    }
}