package com.nexo.ecommerce.orders.order.dto;

/**
 * ShippingAddress
 *     "address": "string",
      "city": "string",
      "phone": "string"
 */
public record ShippingAddress(
    String address,
    String city,
    String phone
) {

}
