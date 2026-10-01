package com.nexo.ecommerce.auth.infrastructure.config;

import com.nexo.ecommerce.auth.application.usecases.ProvisionAdminUserUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrapRunner implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(AdminBootstrapRunner.class);

    private final AdminBootstrapProperties properties;
    private final ProvisionAdminUserUseCase provisionAdminUserUseCase;

    public AdminBootstrapRunner(
            AdminBootstrapProperties properties,
            ProvisionAdminUserUseCase provisionAdminUserUseCase) {
        this.properties = properties;
        this.provisionAdminUserUseCase = provisionAdminUserUseCase;
    }

    @Override
    public void run(String... args) {
        if (!properties.isConfigured()) {
            log.info("Admin bootstrap is disabled; configure AUTH_ADMIN_EMAIL and AUTH_ADMIN_PASSWORD to provision an administrator");
            return;
        }
        String name = properties.name() == null || properties.name().isBlank()
                ? "Nexo Administrator"
                : properties.name();
        boolean created = provisionAdminUserUseCase.execute(name, properties.email(), properties.password());
        log.info(created
                ? "Initial administrator created from bootstrap configuration"
                : "Configured administrator already exists; existing credentials were not changed");
    }
}
