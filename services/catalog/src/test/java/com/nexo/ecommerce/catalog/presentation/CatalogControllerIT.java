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
                                .content("2")
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
}