package com.nexo.ecommerce.orders.infrastructure.persistence;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrdenRepositoryJpa extends JpaRepository<OrderEntityJpa, String> {
    

@Query(
    "SELECT o FROM OrderEntityJpa o WHERE o.userId = :userId"
)
List<OrderEntityJpa> findByUserId(@Param("userId") String userId);


@Query(
    "SELECT o.total FROM OrderEntityJpa o WHERE o.id = :orderId"
)
BigDecimal getOrderTotal(@Param("orderId") String orderId);

}
