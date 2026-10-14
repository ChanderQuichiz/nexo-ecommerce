package com.nexo.ecommerce.notifications.infrastructure.clients.feign;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class AuthFeignConfig {

    public static final String INTERNAL_API_KEY_HEADER = "X-Internal-Api-Key";

    @Bean
    public RequestInterceptor authInternalApiKeyRequestInterceptor(
            @Value("${clients.auth.internal-api-key:}") String internalApiKey) {
        return requestTemplate -> requestTemplate.header(INTERNAL_API_KEY_HEADER, internalApiKey);
    }
}
