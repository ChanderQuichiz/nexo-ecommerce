package com.nexo.ecommerce.orders.infrastructure.persistence;

import com.nexo.ecommerce.orders.domain.entities.Order;
import com.nexo.ecommerce.orders.domain.value_objects.City;
import com.nexo.ecommerce.orders.domain.value_objects.OrderId;
import com.nexo.ecommerce.orders.domain.value_objects.PaymentIntentId;
import com.nexo.ecommerce.orders.domain.value_objects.ShippingFee;
import com.nexo.ecommerce.orders.domain.value_objects.SubTotal;
import com.nexo.ecommerce.orders.domain.value_objects.Total;
import com.nexo.ecommerce.orders.domain.value_objects.UserId;
import com.nexo.ecommerce.orders.domain.value_objects.Address;
import com.nexo.ecommerce.orders.domain.value_objects.Date;
import com.nexo.ecommerce.orders.domain.value_objects.Item;
import com.nexo.ecommerce.orders.domain.value_objects.Phone;
import com.nexo.ecommerce.orders.domain.value_objects.Status;       
import com.nexo.ecommerce.orders.domain.value_objects.Tax;

public class OrderMapper {
    public static OrderEntityJpa toEntity(Order order) {
        OrderEntityJpa orderEntityJpa = new OrderEntityJpa();
        orderEntityJpa.setId(order.getId().value().toString());
        orderEntityJpa.setStatus(order.getStatus().name());
        orderEntityJpa.setUserId(order.getUserId().value().toString());
        orderEntityJpa.setSubtotal(order.getSubtotal().value());
        orderEntityJpa.setShippingFee(order.getShippingFee().value());
        orderEntityJpa.setTax(order.getTax().value());
        orderEntityJpa.setTotal(order.getTotal().value());
        orderEntityJpa.setDate(order.getDate().value());
        orderEntityJpa.setAddress(order.getAddress().value());
        orderEntityJpa.setCity(order.getCity().value());
        orderEntityJpa.setPhone(order.getPhone().value());
        orderEntityJpa.setPaymentIntentId(order.getPaymentIntentId().stream().map(paymentIntentId -> paymentIntentId.value().toString()).toList());
        orderEntityJpa.setItems(order.getItems().stream().map(item -> {
            ItemEmbeddable itemEmbeddable = new ItemEmbeddable();
            itemEmbeddable.setProductId(item.productId());
            itemEmbeddable.setPrice(item.price());
            itemEmbeddable.setQuantity(item.quantity());
            return itemEmbeddable;
        }).toList());
        return orderEntityJpa;
    }
    public static Order toDomain( OrderEntityJpa orderEntityJpa) {
        Order order = new Order(
            new OrderId(orderEntityJpa.getId()),
            new UserId(orderEntityJpa.getUserId()),
            new SubTotal(orderEntityJpa.getSubtotal()),
            new ShippingFee(orderEntityJpa.getShippingFee()),
            new Tax(orderEntityJpa.getTax()),
            new Total(orderEntityJpa.getTotal()),
            Status.valueOf(orderEntityJpa.getStatus()),
            new Date(orderEntityJpa.getDate()),
            new Address(orderEntityJpa.getAddress()),
            new City(orderEntityJpa.getCity()),
            new Phone(orderEntityJpa.getPhone()),
            orderEntityJpa.getPaymentIntentId().stream().map(paymentIntentId -> new PaymentIntentId(paymentIntentId)).toList(),
            orderEntityJpa.getItems().stream().map(itemEmbeddable -> new Item(itemEmbeddable.getPrice(),itemEmbeddable.getProductId(), itemEmbeddable.getQuantity())).toList()
        );
        return order;
    }
  
}
