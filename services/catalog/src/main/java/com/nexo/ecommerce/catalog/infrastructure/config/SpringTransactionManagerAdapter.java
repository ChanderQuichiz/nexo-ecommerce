package com.nexo.ecommerce.catalog.infrastructure.config;

import com.nexo.ecommerce.catalog.application.ports.TransactionManager;

import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Supplier;

@Component
public class SpringTransactionManagerAdapter
        implements TransactionManager {

    private final TransactionTemplate transactionTemplate;

    public SpringTransactionManagerAdapter(
            PlatformTransactionManager transactionManager
    ) {
        this.transactionTemplate =
                new TransactionTemplate(transactionManager);
    }

    @Override
    public <T> T executeInTransaction(
            Supplier<T> operation
    ) {
        return transactionTemplate.execute(
                status -> operation.get()
        );
    }
}