package com.nexo.ecommerce.auth.domain.exception;

public class InvalidAccessTokenException extends RuntimeException {
    public InvalidAccessTokenException() {
        super("Access token is invalid or revoked");
    }
}
