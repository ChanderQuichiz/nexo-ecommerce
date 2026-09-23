package com.nexo.ecommerce.orders.infrastructure.adapters;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.nexo.ecommerce.orders.application.client.CatalogClient;
import com.nexo.ecommerce.orders.application.client.dto.GetProduct;
import com.nexo.ecommerce.orders.application.client.dto.ValidateStockRequest;
import com.nexo.ecommerce.orders.application.client.dto.ValidateStockResponse;

@FeignClient(name = "catalog-service", url = "${catalog.service.url:http://localhost:8081}")
public interface CatalogFeignClient extends CatalogClient {

    @PostMapping("/catalog/validate-stock")
    ValidateStockResponse validateStock(@RequestBody ValidateStockRequest request);


    @GetMapping("/catalog/{id}")
    GetProduct getProductById(@PathVariable Long id);
    

    @org.springframework.web.bind.annotation.PatchMapping("/catalog/{id}/stock")
    Void reduceStock(@PathVariable("id") String id, @RequestBody Integer quantity);

}