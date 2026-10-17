package com.nexo.ecommerce.notifications.infrastructure.clients.feign;

import com.nexo.ecommerce.notifications.application.exceptions.ExternalServiceUnavailableException;
import com.nexo.ecommerce.notifications.application.exceptions.OrderNotFoundException;
import com.nexo.ecommerce.notifications.application.usecases.dto.OrderResponse;
import feign.FeignException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderQueryFeignAdapterTest {

    @Test
    void shouldDelegateOrderIdToFeignClient() {
        OrdersFeignClient client = mock(OrdersFeignClient.class);
        OrderResponse expected = mock(OrderResponse.class);
        String orderId = "72cf900f-7c9e-4bee-a82a-da8499f80ae3";
        when(client.getOrderById(orderId)).thenReturn(expected);
        OrderQueryFeignAdapter adapter = new OrderQueryFeignAdapter(client);

        OrderResponse result = adapter.getOrderById(orderId);

        assertSame(expected, result);
        verify(client).getOrderById(orderId);
    }

    @Test
    void shouldTranslateNotFoundResponse() {
        OrdersFeignClient client = mock(OrdersFeignClient.class);
        String orderId = "72cf900f-7c9e-4bee-a82a-da8499f80ae3";
        when(client.getOrderById(orderId)).thenThrow(feignException(404));
        OrderQueryFeignAdapter adapter = new OrderQueryFeignAdapter(client);

        assertThrows(OrderNotFoundException.class, () -> adapter.getOrderById(orderId));
    }

    @Test
    void shouldTranslateOtherFeignErrorsAsUnavailableService() {
        OrdersFeignClient client = mock(OrdersFeignClient.class);
        String orderId = "72cf900f-7c9e-4bee-a82a-da8499f80ae3";
        when(client.getOrderById(orderId)).thenThrow(feignException(503));
        OrderQueryFeignAdapter adapter = new OrderQueryFeignAdapter(client);

        assertThrows(ExternalServiceUnavailableException.class, () -> adapter.getOrderById(orderId));
    }

    private FeignException feignException(int status) {
        Request request = Request.create(
                Request.HttpMethod.GET,
                "/orders/id",
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
        return FeignException.errorStatus("OrdersFeignClient#getOrderById", response);
    }
}
