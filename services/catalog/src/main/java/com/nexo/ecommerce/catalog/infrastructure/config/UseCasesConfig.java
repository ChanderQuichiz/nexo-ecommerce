package com.nexo.ecommerce.catalog.infrastructure.config;

import com.nexo.ecommerce.catalog.application.ports.TransactionManager;
import com.nexo.ecommerce.catalog.application.repositories.ProductRepository;
import com.nexo.ecommerce.catalog.application.usecases.*;

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

    @Bean
    public CreateProductUseCase createProductUseCase(
            ProductRepository productRepository
    ) {
        return new CreateProductUseCase(productRepository);
    }

    @Bean
    public ListActiveProductsUseCase listActiveProductsUseCase(
            ProductRepository productRepository
    ) {
        return new ListActiveProductsUseCase(productRepository);
    }

    @Bean
    public ListAllProductsUseCase listAllProductsUseCase(
            ProductRepository productRepository
    ) {
        return new ListAllProductsUseCase(productRepository);
    }

    @Bean
    public SearchProductsUseCase searchProductsUseCase(
            ProductRepository productRepository
    ) {
        return new SearchProductsUseCase(productRepository);
    }
}