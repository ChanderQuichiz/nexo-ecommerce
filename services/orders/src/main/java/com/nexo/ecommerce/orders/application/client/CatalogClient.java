package com.nexo.ecommerce.orders.application.client;



import com.nexo.ecommerce.orders.application.client.dto.GetProduct;
import com.nexo.ecommerce.orders.application.client.dto.ValidateStockRequest;
import com.nexo.ecommerce.orders.application.client.dto.ValidateStockResponse;
import com.nexo.ecommerce.orders.application.client.dto.ReduceStockRequest;

public interface CatalogClient {

    ValidateStockResponse validateStock(ValidateStockRequest request);


    GetProduct getProductById( Long id);
    

    Void reduceStock(String id, ReduceStockRequest request);

}