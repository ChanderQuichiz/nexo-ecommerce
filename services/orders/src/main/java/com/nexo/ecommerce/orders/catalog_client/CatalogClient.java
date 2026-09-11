package com.nexo.ecommerce.orders.catalog_client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.nexo.ecommerce.orders.catalog_client.dto.GetProduct;
import com.nexo.ecommerce.orders.catalog_client.dto.ValidateStockRequest;
import com.nexo.ecommerce.orders.catalog_client.dto.ValidateStockResponse;

@FeignClient(name = "catalog-service")
public interface CatalogClient {

    @PostMapping("/catalog/validate-stock")
    ValidateStockResponse validateStock(@RequestBody ValidateStockRequest request);


    @GetMapping("/catalog/{id}")
    GetProduct getProductById(@PathVariable Long id);
    

    @PatchMapping ("/catalog/{id}/stock")
    Void reduceStock(@PathVariable Long id, @RequestBody Integer quantity);

}