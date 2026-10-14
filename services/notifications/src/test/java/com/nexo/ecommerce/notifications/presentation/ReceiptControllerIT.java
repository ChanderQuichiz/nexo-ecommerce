package com.nexo.ecommerce.notifications.presentation;

import com.nexo.ecommerce.notifications.application.exceptions.OrderNotFoundException;
import com.nexo.ecommerce.notifications.application.usecases.GenerateReceiptUseCase;
import com.nexo.ecommerce.notifications.domain.exceptions.OrderNotPaidException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReceiptControllerIT {

    private static final String PATH = "/notifications/generate-receipt/{orderId}";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GenerateReceiptUseCase generateReceiptUseCase;

    @Test
    void shouldReturnPdfFromFullWebContext() throws Exception {
        String orderId = UUID.randomUUID().toString();
        byte[] pdf = "%PDF-integration".getBytes();
        when(generateReceiptUseCase.execute(orderId)).thenReturn(pdf);

        mockMvc.perform(get(PATH, orderId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(content().bytes(pdf));
    }

    @Test
    void shouldReturnConflictFromFullWebContext() throws Exception {
        String orderId = UUID.randomUUID().toString();
        when(generateReceiptUseCase.execute(orderId))
                .thenThrow(new OrderNotPaidException(orderId, "PENDING"));

        mockMvc.perform(get(PATH, orderId))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnNotFoundFromFullWebContext() throws Exception {
        String orderId = UUID.randomUUID().toString();
        when(generateReceiptUseCase.execute(orderId))
                .thenThrow(new OrderNotFoundException(orderId));

        mockMvc.perform(get(PATH, orderId))
                .andExpect(status().isNotFound());
    }
}
