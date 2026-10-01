package com.nexo.ecommerce.orders;
import org.springframework.boot.SpringApplication;
import org.testcontainers.utility.TestcontainersConfiguration;

public class TestOrdersApplication {

    public static void main(String[] args) {
        SpringApplication.from(OrdersApplication::main)
                .with(TestcontainersConfiguration.class)
                .run(args);
    }
}