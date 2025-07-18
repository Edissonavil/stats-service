package com.aec.statssrv.repository;

import com.aec.statssrv.model.OrderFact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Repository
public interface OrderFactRepository extends JpaRepository<OrderFact, Long> {

    @Query("""
        SELECT COALESCE(SUM(f.totalUsd), 0)
        FROM OrderFact f
        WHERE f.createdAt BETWEEN :from AND :to
    """)
    BigDecimal totalUsd(
        @Param("from") Instant from,
        @Param("to")   Instant to
    );

    @Query("""
        SELECT new map(
            f.clienteUsername AS cliente,
            SUM(f.totalUsd)   AS totalUsd
        )
        FROM OrderFact f
        WHERE f.createdAt BETWEEN :from AND :to
        GROUP BY f.clienteUsername
    """)
    List<Map<String, Object>> totalUsdByCliente(
        @Param("from") Instant from,
        @Param("to")   Instant to
    );
}
