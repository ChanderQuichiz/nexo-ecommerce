package com.nexo.ecommerce.orders;

import com.nexo.ecommerce.orders.application.client.CatalogClient;
import com.nexo.ecommerce.orders.application.ports.PaymentPort;
import com.nexo.ecommerce.orders.config.TestcontainersConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
public abstract class BaseIntegrationTest {

    @MockBean
    protected CatalogClient catalogClient;

    @MockBean
    protected PaymentPort paymentPort;
}
