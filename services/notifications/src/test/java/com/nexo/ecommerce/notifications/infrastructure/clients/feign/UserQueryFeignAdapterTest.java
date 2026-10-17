package com.nexo.ecommerce.notifications.infrastructure.clients.feign;

import com.nexo.ecommerce.notifications.application.exceptions.ExternalServiceUnavailableException;
import com.nexo.ecommerce.notifications.application.exceptions.UserNotFoundException;
import com.nexo.ecommerce.notifications.application.usecases.dto.UserResponse;
import feign.FeignException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserQueryFeignAdapterTest {

    @Test
    void shouldDelegateUserIdToFeignClient() {
        AuthFeignClient client = mock(AuthFeignClient.class);
        UserResponse expected = mock(UserResponse.class);
        UUID userId = UUID.randomUUID();
        when(client.getUserById(userId)).thenReturn(expected);
        UserQueryFeignAdapter adapter = new UserQueryFeignAdapter(client);

        UserResponse result = adapter.getUserById(userId);

        assertSame(expected, result);
        verify(client).getUserById(userId);
    }

    @Test
    void shouldTranslateNotFoundResponse() {
        AuthFeignClient client = mock(AuthFeignClient.class);
        UUID userId = UUID.randomUUID();
        when(client.getUserById(userId)).thenThrow(feignException(404));
        UserQueryFeignAdapter adapter = new UserQueryFeignAdapter(client);

        assertThrows(UserNotFoundException.class, () -> adapter.getUserById(userId));
    }

    @Test
    void shouldTranslateOtherFeignErrorsAsUnavailableService() {
        AuthFeignClient client = mock(AuthFeignClient.class);
        UUID userId = UUID.randomUUID();
        when(client.getUserById(userId)).thenThrow(feignException(503));
        UserQueryFeignAdapter adapter = new UserQueryFeignAdapter(client);

        assertThrows(ExternalServiceUnavailableException.class, () -> adapter.getUserById(userId));
    }

    private FeignException feignException(int status) {
        Request request = Request.create(
                Request.HttpMethod.GET,
                "/auth/users/id",
                Map.of(),
                new byte[0],
                StandardCharsets.UTF_8,
                null);
        Response response = Response.builder()
                .status(status)
                .reason("Test response")
                .request(request)
                .headers(Map.of())
                .build();
        return FeignException.errorStatus("AuthFeignClient#getUserById", response);
    }
}
