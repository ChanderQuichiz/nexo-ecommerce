package com.nexo.ecommerce.orders.order;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nexo.ecommerce.orders.order.dto.GetOrderResponse;

import jakarta.transaction.Transactional;

public interface OrderRepository extends JpaRepository<OrderEntity, String> {
    
    @Query("""
    SELECT new com.nexo.ecommerce.orders.order.dto.GetOrderResponse(
        o.id,
        o.userId,
        o.subtotal,
        o.shippingFee,
        o.tax,
        o.total,
        o.status,
        o.date,
        null,
        o.address,
        o.city,
        o.phone
    )
    FROM OrderEntity o
    WHERE o.id = :orderId
""")
    GetOrderResponse findOrderById(@Param("orderId") String orderId);

    @Query("""
    SELECT new com.nexo.ecommerce.orders.order.dto.GetOrderResponse(
        o.id,
        o.userId,
        o.subtotal,
        o.shippingFee,
        o.tax,
        o.total,
        o.status,
        o.date,
        null,
        o.address,
        o.city,
        o.phone
    )
    FROM OrderEntity o
""")
    List<GetOrderResponse> getAllOrders();

    @Query("""
    SELECT new com.nexo.ecommerce.orders.order.dto.GetOrderResponse(
        o.id,
        o.userId,
        o.subtotal,
        o.shippingFee,
        o.tax,
        o.total,
        o.status,
        o.date,
        null,
        o.address,
        o.city,
        o.phone
    )
    FROM OrderEntity o
    WHERE o.userId = :userId
""")
    List<GetOrderResponse> getAllOrdersByUserId(@Param("userId") String userId);

    @Modifying
    @Transactional
    @Query ("""
    UPDATE OrderEntity o
    SET o.status = :status
    WHERE o.id = :orderId
""")
    void updateOrderStatus(@Param("orderId") String orderId, @Param("status") String status);

    @Query ("""
    SELECT o
    FROM OrderEntity o
    WHERE o.stripePaymentIntentId = :paymentIntentId
""")
    Optional<OrderEntity> findByStripePaymentIntentId(String paymentIntentId);
}
