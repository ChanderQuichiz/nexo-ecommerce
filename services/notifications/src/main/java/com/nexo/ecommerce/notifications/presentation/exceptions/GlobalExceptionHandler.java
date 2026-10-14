package com.nexo.ecommerce.notifications.presentation.exceptions;

import com.nexo.ecommerce.notifications.application.exceptions.ExternalServiceUnavailableException;
import com.nexo.ecommerce.notifications.application.exceptions.OrderNotFoundException;
import com.nexo.ecommerce.notifications.application.exceptions.UserNotFoundException;
import com.nexo.ecommerce.notifications.domain.exceptions.OrderNotPaidException;
import com.nexo.ecommerce.notifications.infrastructure.pdf.ReceiptGenerationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleBadRequest(
            IllegalArgumentException exception,
            HttpServletRequest request) {
        return response(HttpStatus.BAD_REQUEST, "Invalid request data", request);
    }

    @ExceptionHandler({OrderNotFoundException.class, UserNotFoundException.class})
    public ResponseEntity<ApiError> handleNotFound(
            RuntimeException exception,
            HttpServletRequest request) {
        return response(HttpStatus.NOT_FOUND, exception.getMessage(), request);
    }

    @ExceptionHandler(OrderNotPaidException.class)
    public ResponseEntity<ApiError> handleOrderNotPaid(
            OrderNotPaidException exception,
            HttpServletRequest request) {
        return response(HttpStatus.CONFLICT, exception.getMessage(), request);
    }

    @ExceptionHandler(ExternalServiceUnavailableException.class)
    public ResponseEntity<ApiError> handleExternalServiceUnavailable(
            ExternalServiceUnavailableException exception,
            HttpServletRequest request) {
        return response(HttpStatus.BAD_GATEWAY, exception.getMessage(), request);
    }

    @ExceptionHandler(ReceiptGenerationException.class)
    public ResponseEntity<ApiError> handleReceiptGeneration(
            ReceiptGenerationException exception,
            HttpServletRequest request) {
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to generate receipt", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(
            Exception exception,
            HttpServletRequest request) {
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected internal error", request);
    }

    private ResponseEntity<ApiError> response(
            HttpStatus status,
            String message,
            HttpServletRequest request) {
        ApiError error = new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }
}
