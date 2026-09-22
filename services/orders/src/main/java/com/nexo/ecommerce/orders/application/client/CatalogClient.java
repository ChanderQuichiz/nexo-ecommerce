package com.nexo.ecommerce.orders.application.client;



import com.nexo.ecommerce.orders.application.client.dto.GetProduct;
import com.nexo.ecommerce.orders.application.client.dto.ValidateStockRequest;
import com.nexo.ecommerce.orders.application.client.dto.ValidateStockResponse;

public interface CatalogClient {

    ValidateStockResponse validateStock(ValidateStockRequest request);


    GetProduct getProductById( Long id);
    

    Void reduceStock( String id,Integer quantity);

}