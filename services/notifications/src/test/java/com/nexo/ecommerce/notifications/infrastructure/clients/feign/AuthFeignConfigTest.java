package com.nexo.ecommerce.notifications.infrastructure.clients.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.openfeign.FeignClient;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthFeignConfigTest {

    @Test
    void shouldAddInternalApiKeyHeaderWithConfiguredValue() {
        String apiKey = "test-internal-key";
        RequestInterceptor interceptor = new AuthFeignConfig()
                .authInternalApiKeyRequestInterceptor(apiKey);
        RequestTemplate template = new RequestTemplate();

        interceptor.apply(template);

        Collection<String> values = template.headers().get(AuthFeignConfig.INTERNAL_API_KEY_HEADER);
        assertEquals(1, values.size());
        assertTrue(values.contains(apiKey));
    }

    @Test
    void shouldApplyAuthConfigurationOnlyToAuthClient() {
        FeignClient authAnnotation = AuthFeignClient.class.getAnnotation(FeignClient.class);
        FeignClient ordersAnnotation = OrdersFeignClient.class.getAnnotation(FeignClient.class);

        assertTrue(Arrays.asList(authAnnotation.configuration()).contains(AuthFeignConfig.class));
        assertFalse(Arrays.asList(ordersAnnotation.configuration()).contains(AuthFeignConfig.class));
    }
}
