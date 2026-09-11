package com.nexo.ecommerce.orders.order_items;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nexo.ecommerce.orders.order.dto.CreateOrderItem;
import com.nexo.ecommerce.orders.order.dto.OrderItem;
import com.nexo.ecommerce.orders.order.dto.OrderItemRow;

public interface OrderItemsRepository extends JpaRepository<OrderItemsEntity, String> {
    
    @Query("""
    SELECT new com.nexo.ecommerce.orders.order.dto.OrderItem(
        i.productId,
        i.name,
        i.price,
        i.quantity
    )
    FROM OrderItemsEntity i
    WHERE i.orderId = :orderId
""")
    List<OrderItem> findItemsByOrderId(@Param("orderId") String orderId);

    @Query("""
    SELECT new com.nexo.ecommerce.orders.order.dto.OrderItemRow(
        i.productId,
        i.name,
        i.price,
        i.quantity,
        i.orderId
    )
    FROM OrderItemsEntity i
    WHERE i.orderId IN :orderIds
""")
    List<OrderItemRow> findItemsByOrderIds(@Param("orderIds") List<String> orderIds);

    @Query("""
    SELECT new com.nexo.ecommerce.orders.order.dto.CreateOrderItem(
        i.productId,
        i.quantity
    )
    FROM OrderItemsEntity i
    WHERE i.orderId = :orderId
""")
    List<CreateOrderItem> findByOrderId(@Param("orderId") String orderId);
}
