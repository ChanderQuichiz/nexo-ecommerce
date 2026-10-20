package com.nexo.ecommerce.orders.infrastructure.adapters;

import com.nexo.ecommerce.orders.application.client.dto.GetProduct;
import com.nexo.ecommerce.orders.application.client.dto.ReduceStockRequest;
import com.nexo.ecommerce.orders.application.client.dto.ValidateStockRequest;
import com.nexo.ecommerce.orders.application.client.dto.ValidateStockResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CatalogFeignClientFallback implements CatalogFeignClient {

    private static final Logger log = LoggerFactory.getLogger(CatalogFeignClientFallback.class);

    @Override
    public ValidateStockResponse validateStock(ValidateStockRequest request) {
        log.error("Circuit Breaker Abierto: Catalog Service no esta disponible para validar stock");
        throw new IllegalStateException("El servicio de Catalogo no esta disponible actualmente. Intenta nuevamente mas tarde.");
    }

    @Override
    public GetProduct getProductById(Long id) {
        log.error("Circuit Breaker Abierto: Catalog Service no esta disponible para obtener producto {}", id);
        throw new IllegalStateException("El servicio de Catalogo no esta disponible actualmente. Intenta nuevamente mas tarde.");
    }

    @Override
    public Void reduceStock(String id, ReduceStockRequest request) {
        log.error("Circuit Breaker Abierto: Catalog Service no esta disponible para reducir stock del producto {}", id);
        throw new IllegalStateException("El servicio de Catalogo no esta disponible actualmente. Intenta nuevamente mas tarde.");
    }
}
