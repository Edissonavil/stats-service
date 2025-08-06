// src/main/java/com/aec/statssrv/repository/StatsRepository.java
package com.aec.statssrv.repository;

import com.aec.statssrv.dto.CollaboratorSalesDto;
import com.aec.statssrv.dto.MonthlySalesDto;
import com.aec.statssrv.dto.PaymentMethodStatsDto;
import com.aec.statssrv.dto.ProductSalesDto;
import com.aec.statssrv.model.Order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Order, Long> {

    // Estadísticas generales para admin (JPQL - solo Order)
    @Query("SELECT COALESCE(SUM(o.total), 0) FROM Order o " +
            "WHERE o.status = 'COMPLETED' " +
            "AND o.creadoEn >= :startDate " +
            "AND o.creadoEn <= :endDate")
    BigDecimal getTotalRevenue(@Param("startDate") LocalDateTime startDate,
                               @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(o) FROM Order o " +
            "WHERE o.status = 'COMPLETED' " +
            "AND o.creadoEn >= :startDate " +
            "AND o.creadoEn <= :endDate")
    Long getTotalCompletedOrders(@Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate);

    // Contar productos (unidades) vendidas (JPQL - solo OrderItem y Order)
    @Query("SELECT COUNT(DISTINCT oi.productId) FROM OrderItem oi " + // Mantengo COUNT(DISTINCT oi.productId) según tu código original
            "JOIN oi.order o " +
            "WHERE o.status = 'COMPLETED' " +
            "AND o.creadoEn >= :startDate " +
            "AND o.creadoEn <= :endDate")
    Long getTotalProductsSold(@Param("startDate") LocalDateTime startDate,
                              @Param("endDate") LocalDateTime endDate);

    // Ventas por colaborador (SQL Nativo - usa tabla 'products' para uploader_username y pais)
    @Query(value = "SELECT " +
            "p.uploader_username AS uploaderUsername, " +
            "COALESCE(SUM(oi.precio_unitario * oi.cantidad), 0) AS totalSales, " +
            "COALESCE(SUM(oi.cantidad), 0) AS totalQuantity, " +
            "COUNT(DISTINCT o.id) AS ordersCount, " +
            "p.pais AS country " +
            "FROM order_items oi " +
            "JOIN orders o ON oi.order_id = o.id " +
            "JOIN products p ON oi.product_id = p.id_producto " + // Unir con la tabla products
            "WHERE o.status = 'COMPLETED' " +
            "AND o.creado_en >= :startDate " +
            "AND o.creado_en <= :endDate " +
            "GROUP BY p.uploader_username, p.pais " +
            "ORDER BY totalSales DESC",
            nativeQuery = true)
    List<CollaboratorSalesDto> getCollaboratorSales(@Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate);

    // Ventas por producto (SQL Nativo - usa tabla 'products')
    @Query(value = "SELECT " +
            "p.id_producto AS idProducto, " +
            "p.nombre AS productName, " +
            "p.uploader_username AS uploaderUsername, " +
            "COALESCE(SUM(oi.precio_unitario * oi.cantidad), 0) AS totalSales, " +
            "COALESCE(SUM(oi.cantidad), 0) AS totalQuantity, " +
            "COUNT(DISTINCT o.id) AS ordersCount, " +
            "p.precio_individual AS unitPrice, " +
            "p.pais AS country " +
            "FROM order_items oi " +
            "JOIN orders o ON oi.order_id = o.id " +
            "JOIN products p ON oi.product_id = p.id_producto " + // Unir con la tabla products
            "WHERE o.status = 'COMPLETED' " +
            "AND o.creado_en >= :startDate " +
            "AND o.creado_en <= :endDate " +
            "AND (:uploaderUsername IS NULL OR p.uploader_username = :uploaderUsername) " + // Usar uploader_username de products
            "GROUP BY p.id_producto, p.nombre, p.uploader_username, p.precio_individual, p.pais " +
            "ORDER BY totalSales DESC",
            nativeQuery = true)
    List<ProductSalesDto> getProductSales(@Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate,
                                          @Param("uploaderUsername") String uploaderUsername);

    // Estadísticas por método de pago (SQL Nativo - usa tabla 'products' para filtrar por uploader_username)
    @Query(value = "SELECT " +
            "COALESCE(o.payment_method, 'NO_ESPECIFICADO') AS paymentMethod, " +
            "COALESCE(SUM(oi.precio_unitario * oi.cantidad), 0) AS totalAmount, " +
            "COUNT(DISTINCT o.id) AS ordersCount " +
            "FROM order_items oi " +
            "JOIN orders o ON oi.order_id = o.id " +
            "LEFT JOIN products p ON oi.product_id = p.id_producto " + // LEFT JOIN para que no se filtren si product_id es nulo
            "WHERE o.status = 'COMPLETED' " +
            "AND o.creado_en >= :startDate " +
            "AND o.creado_en <= :endDate " +
            "AND (:uploaderUsername IS NULL OR p.uploader_username = :uploaderUsername) " + // Usar uploader_username de products
            "GROUP BY o.payment_method " +
            "ORDER BY totalAmount DESC",
            nativeQuery = true)
    List<PaymentMethodStatsDto> getPaymentMethodStats(@Param("startDate") LocalDateTime startDate,
                                                      @Param("endDate") LocalDateTime endDate,
                                                      @Param("uploaderUsername") String uploaderUsername);

    // Estadísticas específicas para colaborador (SQL Nativo - usa tabla 'products')
    @Query(value = "SELECT COALESCE(SUM(oi.precio_unitario * oi.cantidad), 0) FROM order_items oi " +
            "JOIN orders o ON oi.order_id = o.id " +
            "JOIN products p ON oi.product_id = p.id_producto " + // Unir con la tabla products
            "WHERE o.status = 'COMPLETED' " +
            "AND p.uploader_username = :uploaderUsername " + // Usar uploader_username de products
            "AND o.creado_en >= :startDate " +
            "AND o.creado_en <= :endDate",
            nativeQuery = true)
    BigDecimal getCollaboratorTotalRevenue(@Param("uploaderUsername") String uploaderUsername,
                                           @Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);

    @Query(value = "SELECT COUNT(DISTINCT o.id) FROM order_items oi " +
            "JOIN orders o ON oi.order_id = o.id " +
            "JOIN products p ON oi.product_id = p.id_producto " + // Unir con la tabla products
            "WHERE o.status = 'COMPLETED' " +
            "AND p.uploader_username = :uploaderUsername " + // Usar uploader_username de products
            "AND o.creado_en >= :startDate " +
            "AND o.creado_en <= :endDate",
            nativeQuery = true)
    Long getCollaboratorTotalOrders(@Param("uploaderUsername") String uploaderUsername,
                                    @Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate);

    // Contar productos (unidades) vendidas por colaborador (SQL Nativo - usa tabla 'products')
    @Query(value = "SELECT COUNT(DISTINCT oi.product_id) FROM order_items oi " + // Mantengo COUNT(DISTINCT oi.product_id)
            "JOIN orders o ON oi.order_id = o.id " +
            "JOIN products p ON oi.product_id = p.id_producto " + // Unir con la tabla products
            "WHERE o.status = 'COMPLETED' " +
            "AND p.uploader_username = :uploaderUsername " + // Usar uploader_username de products
            "AND o.creado_en >= :startDate " +
            "AND o.creado_en <= :endDate",
            nativeQuery = true)
    Long getCollaboratorTotalProductsSold(@Param("uploaderUsername") String uploaderUsername,
                                         @Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);

    // Verificar si el colaborador existe (SQL Nativo - usa tabla 'users')
    @Query(value = "SELECT COUNT(u.id) > 0 FROM users u WHERE u.nombre_usuario = :username AND u.rol = 'ROL_COLABORADOR'",
            nativeQuery = true)
    boolean existsCollaboratorByUsername(@Param("username") String username);

    // --- Nuevas Consultas para el Panel de Administrador ---

    // Total de colaboradores (SQL Nativo - usa tabla 'users')
    @Query(value = "SELECT COUNT(u.id) FROM users u WHERE u.rol = 'ROL_COLABORADOR'",
            nativeQuery = true)
    Long countTotalCollaborators();

    // Total de clientes (JPQL - solo Order)
    @Query("SELECT COUNT(DISTINCT o.clienteUsername) FROM Order o WHERE o.status = 'COMPLETED'") // Usar clienteUsername de Order
    Long countTotalCustomers();

    // Productos pendientes de revisión (SQL Nativo - usa tabla 'products')
    @Query(value = "SELECT COUNT(p.id_producto) FROM products p WHERE p.estado = 'PENDIENTE'", // Usar p.estado
            nativeQuery = true)
    Integer countProductsPendingReview();

    // Pagos pendientes de verificación (JPQL - solo Order)
    @Query("SELECT COUNT(o) FROM Order o WHERE o.paymentStatus = 'UPLOADED_RECEIPT'") // Usar valor de enum
    Integer countPaymentsToVerify();

    // Errores en comprobantes de pago (JPQL - solo Order)
    @Query("SELECT COUNT(o) FROM Order o WHERE o.paymentStatus = 'PAYMENT_REJECTED'") // Usar valor de enum
    Integer countPaymentErrors();

    // Total de productos registrados en el sistema (SQL Nativo - usa tabla 'products')
    @Query(value = "SELECT COUNT(p.id_producto) FROM products p",
            nativeQuery = true)
    Long countTotalProducts();

    // Ventas mensuales para el gráfico de línea (JPQL - solo Order)
    @Query("SELECT new com.aec.statssrv.dto.MonthlySalesDto(FUNCTION('MONTH', o.creadoEn), COALESCE(SUM(o.total), 0)) " +
            "FROM Order o " +
            "WHERE o.status = 'COMPLETED' " +
            "AND FUNCTION('YEAR', o.creadoEn) = :year " +
            "GROUP BY FUNCTION('MONTH', o.creadoEn) " +
            "ORDER BY FUNCTION('MONTH', o.creadoEn)")
    List<MonthlySalesDto> getMonthlySales(@Param("year") Integer year);

    // Obtener ingresos de un mes específico para el cálculo de crecimiento (JPQL - solo Order)
    @Query("SELECT COALESCE(SUM(o.total), 0) FROM Order o " +
            "WHERE o.status = 'COMPLETED' " +
            "AND FUNCTION('YEAR', o.creadoEn) = :year " +
            "AND FUNCTION('MONTH', o.creadoEn) = :month")
    BigDecimal getMonthlyRevenue(@Param("year") Integer year, @Param("month") Integer month);

    // Top productos vendidos en los últimos 30 días (SQL Nativo - usa tabla 'products')
    @Query(value = "SELECT " +
            "p.id_producto AS idProducto, " +
            "p.nombre AS productName, " +
            "p.uploader_username AS uploaderUsername, " +
            "COALESCE(SUM(oi.precio_unitario * oi.cantidad), 0) AS totalSales, " +
            "COALESCE(SUM(oi.cantidad), 0) AS totalQuantity, " +
            "COUNT(DISTINCT o.id) AS ordersCount, " +
            "p.precio_individual AS unitPrice, " +
            "p.pais AS country " +
            "FROM order_items oi " +
            "JOIN orders o ON oi.order_id = o.id " +
            "JOIN products p ON oi.product_id = p.id_producto " + // Unir con la tabla products
            "WHERE o.status = 'COMPLETED' " +
            "AND o.creado_en >= :startDate " +
            "AND o.creado_en <= :endDate " +
            "GROUP BY p.id_producto, p.nombre, p.uploader_username, p.precio_individual, p.pais " +
            "ORDER BY totalSales DESC",
            nativeQuery = true)
    List<ProductSalesDto> getTopProductsLast30Days(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Ventas mensuales por colaborador para el gráfico de línea (SQL Nativo - usa tabla 'products')
    @Query(value = "SELECT " +
            "FUNCTION('MONTH', o.creado_en) AS month, " + // Usar nombre de columna de DB
            "COALESCE(SUM(oi.precio_unitario * oi.cantidad), 0) AS revenue " +
            "FROM order_items oi " +
            "JOIN orders o ON oi.order_id = o.id " +
            "JOIN products p ON oi.product_id = p.id_producto " + // Unir con la tabla products
            "WHERE o.status = 'COMPLETED' " +
            "AND p.uploader_username = :uploaderUsername " + // Usar uploader_username de products
            "AND FUNCTION('YEAR', o.creado_en) = :year " + // Usar nombre de columna de DB
            "GROUP BY FUNCTION('MONTH', o.creado_en) " +
            "ORDER BY FUNCTION('MONTH', o.creado_en)",
            nativeQuery = true)
    List<MonthlySalesDto> getMonthlySalesByCollaborator(@Param("uploaderUsername") String uploaderUsername, @Param("year") Integer year);
}
