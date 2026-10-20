package com.nexo.ecommerce.orders.infrastructure.adapters;

import com.nexo.ecommerce.orders.application.client.dto.GetProduct;
import com.nexo.ecommerce.orders.application.client.dto.ReduceStockRequest;
import com.nexo.ecommerce.orders.application.client.dto.ValidateStockRequest;
import com.nexo.ecommerce.orders.application.client.dto.ValidateStockResponse;
import org.springframework.stereotype.Component;

@Component
public class CatalogFeignClientFallback implements CatalogFeignClient {

    @Override
    public ValidateStockResponse validateStock(ValidateStockRequest request) {
        throw new IllegalStateException("El servicio de Catálogo no está disponible actualmente. Intenta nuevamente más tarde.");
    }

    @Override
    public GetProduct getProductById(Long id) {
        throw new IllegalStateException("El servicio de Catálogo no está disponible actualmente. Intenta nuevamente más tarde.");
    }

    @Override
    public Void reduceStock(String id, ReduceStockRequest request) {
        throw new IllegalStateException("El servicio de Catálogo no está disponible actualmente. Intenta nuevamente más tarde.");
    }
}
