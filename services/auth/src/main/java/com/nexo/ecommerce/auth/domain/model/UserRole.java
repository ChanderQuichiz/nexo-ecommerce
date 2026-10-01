package com.nexo.ecommerce.auth.domain.model;

public enum UserRole {
    ADMIN("Admin"),
    CLIENT("Client");

    private final String apiValue;

    UserRole(String apiValue) {
        this.apiValue = apiValue;
    }

    public String apiValue() {
        return apiValue;
    }
}
