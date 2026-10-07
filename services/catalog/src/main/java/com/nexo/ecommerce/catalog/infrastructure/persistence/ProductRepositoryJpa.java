package com.nexo.ecommerce.catalog.infrastructure.persistence;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepositoryJpa
        extends JpaRepository<ProductEntityJpa, String> {

    List<ProductEntityJpa> findAllByActiveTrueOrderByNameAsc();

    boolean existsByNameIgnoreCase(String name);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT product
            FROM ProductEntityJpa product
            WHERE product.id = :productId
            """)
    Optional<ProductEntityJpa> findByIdForStockUpdate(
            @Param("productId") String productId
    );

    @Query("""
    SELECT p
    FROM ProductEntityJpa p
    WHERE p.active = true
      AND (
          :search IS NULL
          OR LOWER(p.name) LIKE LOWER(
              CONCAT('%', CAST(:search AS string), '%')
          )
      )
      AND (
          :category IS NULL
          OR LOWER(p.category) = LOWER(
              CAST(:category AS string)
          )
      )
    ORDER BY p.name
    """)
    List<ProductEntityJpa> searchActive(
            @Param("search") String search,
            @Param("category") String category
    );

    boolean existsByNameIgnoreCaseAndIdNot(
            String name,
            String productId
    );
}