package com.nexo.ecommerce.catalog.infrastructure.config;

import com.nexo.ecommerce.catalog.application.ports.TransactionManager;
import com.nexo.ecommerce.catalog.application.repositories.ProductRepository;
import com.nexo.ecommerce.catalog.application.usecases.GetProductByIdUseCase;
import com.nexo.ecommerce.catalog.application.usecases.ReduceStockUseCase;
import com.nexo.ecommerce.catalog.application.usecases.ValidateStockUseCase;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCasesConfig {

    @Bean
    public GetProductByIdUseCase getProductByIdUseCase(
            ProductRepository productRepository
    ) {
        return new GetProductByIdUseCase(productRepository);
    }

    @Bean
    public ValidateStockUseCase validateStockUseCase(
            ProductRepository productRepository
    ) {
        return new ValidateStockUseCase(productRepository);
    }

    @Bean
    public ReduceStockUseCase reduceStockUseCase(
            ProductRepository productRepository,
            TransactionManager transactionManager
    ) {
        return new ReduceStockUseCase(
                productRepository,
                transactionManager
        );
    }
}