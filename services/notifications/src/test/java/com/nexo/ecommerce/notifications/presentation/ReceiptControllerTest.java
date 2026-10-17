package com.nexo.ecommerce.notifications.presentation;

import com.nexo.ecommerce.notifications.application.exceptions.ExternalServiceUnavailableException;
import com.nexo.ecommerce.notifications.application.exceptions.OrderNotFoundException;
import com.nexo.ecommerce.notifications.application.usecases.GenerateReceiptUseCase;
import com.nexo.ecommerce.notifications.domain.exceptions.OrderNotPaidException;
import com.nexo.ecommerce.notifications.infrastructure.pdf.ReceiptGenerationException;
import com.nexo.ecommerce.notifications.presentation.controllers.ReceiptController;
import com.nexo.ecommerce.notifications.presentation.exceptions.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReceiptController.class)
@Import(GlobalExceptionHandler.class)
class ReceiptControllerTest {

    private static final String PATH = "/notifications/generate-receipt/{orderId}";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GenerateReceiptUseCase generateReceiptUseCase;

    @Test
    void shouldReturnGeneratedPdfAsAttachment() throws Exception {
        String orderId = UUID.randomUUID().toString();
        byte[] pdf = "%PDF-test".getBytes();
        when(generateReceiptUseCase.execute(orderId)).thenReturn(pdf);

        mockMvc.perform(get(PATH, orderId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string(
                        "Content-Disposition",
                        "attachment; filename=\"receipt-" + orderId + ".pdf\""))
                .andExpect(content().bytes(pdf));
    }

    @Test
    void shouldReturnBadRequestAndApiErrorForInvalidUuid() throws Exception {
        mockMvc.perform(get(PATH, "not-a-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Invalid request data"))
                .andExpect(jsonPath("$.path").value("/notifications/generate-receipt/not-a-uuid"));
        verifyNoInteractions(generateReceiptUseCase);
    }

    @Test
    void shouldReturnNotFoundWhenOrderDoesNotExist() throws Exception {
        String orderId = UUID.randomUUID().toString();
        when(generateReceiptUseCase.execute(orderId)).thenThrow(new OrderNotFoundException(orderId));

        mockMvc.perform(get(PATH, orderId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void shouldReturnConflictWhenOrderIsNotPaid() throws Exception {
        String orderId = UUID.randomUUID().toString();
        when(generateReceiptUseCase.execute(orderId))
                .thenThrow(new OrderNotPaidException(orderId, "PENDING"));

        mockMvc.perform(get(PATH, orderId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void shouldReturnBadGatewayWhenExternalServiceIsUnavailable() throws Exception {
        String orderId = UUID.randomUUID().toString();
        when(generateReceiptUseCase.execute(orderId)).thenThrow(
                new ExternalServiceUnavailableException("Orders Service", new RuntimeException()));

        mockMvc.perform(get(PATH, orderId))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.status").value(502))
                .andExpect(jsonPath("$.message", containsString("Orders Service")));
    }

    @Test
    void shouldReturnInternalServerErrorForReceiptGenerationFailure() throws Exception {
        String orderId = UUID.randomUUID().toString();
        when(generateReceiptUseCase.execute(orderId))
                .thenThrow(new ReceiptGenerationException(new RuntimeException()));

        mockMvc.perform(get(PATH, orderId))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Unable to generate receipt"));
    }

    @Test
    void shouldReturnSanitizedInternalServerErrorForUnexpectedFailure() throws Exception {
        String orderId = UUID.randomUUID().toString();
        when(generateReceiptUseCase.execute(orderId))
                .thenThrow(new RuntimeException("sensitive internal detail"));

        mockMvc.perform(get(PATH, orderId))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Unexpected internal error"))
                .andExpect(content().string(org.hamcrest.Matchers.not(containsString("sensitive"))));
    }
}
