// src/main/java/com/aec/statssrv/repository/StatsRepository.java
package com.aec.statssrv.repository;

import com.aec.statssrv.dto.CollaboratorSalesDto;
import com.aec.statssrv.dto.MonthlySalesDto;
import com.aec.statssrv.dto.PaymentMethodStatsDto;
import com.aec.statssrv.dto.ProductSalesDto;
import com.aec.statssrv.model.Order;
import com.aec.statssrv.model.Product;
import com.aec.statssrv.model.OrderItem;

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

       // Cantidad de productos distintos vendidos (JPQL - solo OrderItem y Order)
       @Query("SELECT COUNT(DISTINCT oi.productId) FROM OrderItem oi " +
                     "JOIN oi.order o " +
                     "WHERE o.status = 'COMPLETED' " +
                     "AND o.creadoEn >= :startDate " +
                     "AND o.creadoEn <= :endDate")
       Long getTotalProductsSold(@Param("startDate") LocalDateTime startDate,
                     @Param("endDate") LocalDateTime endDate);

       // Ventas por colaborador (JPQL - OrderItem y Product)
       @Query(value = "SELECT new com.aec.statssrv.dto.CollaboratorSalesDto(" +
                     "p.uploaderUsername, " +
                     "COALESCE(SUM(oi.precioUnitario * oi.cantidad), 0), " +
                     "COALESCE(SUM(oi.cantidad), 0), " +
                     "COUNT(DISTINCT o.id), " +
                     "p.pais) " +
                     "FROM OrderItem oi " +
                     "JOIN oi.order o " +
                     "JOIN oi.product p " +
                     "WHERE o.status = 'COMPLETED' " +
                     "AND o.creadoEn >= :startDate " +
                     "AND o.creadoEn <= :endDate " +
                     "GROUP BY p.uploaderUsername, p.pais " +
                     "ORDER BY SUM(oi.precioUnitario * oi.cantidad) DESC", countQuery = "SELECT COUNT(DISTINCT p.uploaderUsername, p.pais) "
                                   + // CONTEO EXPLÍCITO
                                   "FROM OrderItem oi " +
                                   "JOIN oi.order o " +
                                   "JOIN oi.product p " +
                                   "WHERE o.status = 'COMPLETED' " +
                                   "AND o.creadoEn >= :startDate " +
                                   "AND o.creadoEn <= :endDate")
       List<CollaboratorSalesDto> getCollaboratorSales(@Param("startDate") LocalDateTime startDate,
                     @Param("endDate") LocalDateTime endDate);

       // Ventas por producto (JPQL - OrderItem y Product)
       @Query(value = "SELECT new com.aec.statssrv.dto.ProductSalesDto(" +
                     "p.idProducto, " +
                     "p.nombre, " +
                     "p.uploaderUsername, " +
                     "COALESCE(SUM(oi.precioUnitario * oi.cantidad), 0), " +
                     "COALESCE(SUM(oi.cantidad), 0), " +
                     "COUNT(DISTINCT o.id), " +
                     "p.precioIndividual, " +
                     "p.pais) " +
                     "FROM OrderItem oi " +
                     "JOIN oi.order o " +
                     "JOIN oi.product p " +
                     "WHERE o.status = 'COMPLETED' " +
                     "AND o.creadoEn >= :startDate " +
                     "AND o.creadoEn <= :endDate " +
                     "AND (:uploaderUsername IS NULL OR p.uploaderUsername = :uploaderUsername) " +
                     "GROUP BY p.idProducto, p.nombre, p.uploaderUsername, p.precioIndividual, p.pais " +
                     "ORDER BY SUM(oi.precioUnitario * oi.cantidad) DESC", countQuery = "SELECT COUNT(DISTINCT p.idProducto) "
                                   + // CONTEO EXPLÍCITO
                                   "FROM OrderItem oi " +
                                   "JOIN oi.order o " +
                                   "JOIN oi.product p " +
                                   "WHERE o.status = 'COMPLETED' " +
                                   "AND o.creadoEn >= :startDate " +
                                   "AND o.creadoEn <= :endDate " +
                                   "AND (:uploaderUsername IS NULL OR p.uploaderUsername = :uploaderUsername)")
       List<ProductSalesDto> getProductSales(@Param("startDate") LocalDateTime startDate,
                     @Param("endDate") LocalDateTime endDate,
                     @Param("uploaderUsername") String uploaderUsername);

       // Estadísticas por método de pago (JPQL - OrderItem y Product)
       @Query(value = "SELECT new com.aec.statssrv.dto.PaymentMethodStatsDto(" +
                     "COALESCE(o.paymentMethod, 'NO_ESPECIFICADO'), " +
                     "COALESCE(SUM(oi.precioUnitario * oi.cantidad), 0), " +
                     "COUNT(DISTINCT o.id)) " +
                     "FROM OrderItem oi " +
                     "JOIN oi.order o " +
                     "JOIN oi.product p " +
                     "WHERE o.status = 'COMPLETED' " +
                     "AND o.creadoEn >= :startDate " +
                     "AND o.creadoEn <= :endDate " +
                     "AND (:uploaderUsername IS NULL OR p.uploaderUsername = :uploaderUsername) " +
                     "GROUP BY o.paymentMethod " +
                     "ORDER BY SUM(oi.precioUnitario * oi.cantidad) DESC", countQuery = "SELECT COUNT(DISTINCT o.paymentMethod) "
                                   + // CONTEO EXPLÍCITO
                                   "FROM OrderItem oi " +
                                   "JOIN oi.order o " +
                                   "JOIN oi.product p " +
                                   "WHERE o.status = 'COMPLETED' " +
                                   "AND o.creadoEn >= :startDate " +
                                   "AND o.creadoEn <= :endDate " +
                                   "AND (:uploaderUsername IS NULL OR p.uploaderUsername = :uploaderUsername)")
       List<PaymentMethodStatsDto> getPaymentMethodStats(@Param("startDate") LocalDateTime startDate,
                     @Param("endDate") LocalDateTime endDate,
                     @Param("uploaderUsername") String uploaderUsername);

       // Estadísticas específicas para colaborador (JPQL - OrderItem y Product)
       @Query("SELECT COALESCE(SUM(oi.precioUnitario * oi.cantidad), 0) FROM OrderItem oi " +
                     "JOIN oi.order o " +
                     "JOIN oi.product p " +
                     "WHERE o.status = 'COMPLETED' " +
                     "AND p.uploaderUsername = :uploaderUsername " +
                     "AND o.creadoEn >= :startDate " +
                     "AND o.creadoEn <= :endDate")
       BigDecimal getCollaboratorTotalRevenue(@Param("uploaderUsername") String uploaderUsername,
                     @Param("startDate") LocalDateTime startDate,
                     @Param("endDate") LocalDateTime endDate);

       @Query("SELECT COUNT(DISTINCT o.id) FROM OrderItem oi " +
                     "JOIN oi.order o " +
                     "JOIN oi.product p " +
                     "WHERE o.status = 'COMPLETED' " +
                     "AND p.uploaderUsername = :uploaderUsername " +
                     "AND o.creadoEn >= :startDate " +
                     "AND o.creadoEn <= :endDate")
       Long getCollaboratorTotalOrders(@Param("uploaderUsername") String uploaderUsername,
                     @Param("startDate") LocalDateTime startDate,
                     @Param("endDate") LocalDateTime endDate);

       // Cantidad de productos distintos vendidos por colaborador (JPQL - OrderItem y
       // Product)
       @Query("SELECT COUNT(DISTINCT oi.productId) FROM OrderItem oi " +
                     "JOIN oi.order o " +
                     "JOIN oi.product p " +
                     "WHERE o.status = 'COMPLETED' " +
                     "AND p.uploaderUsername = :uploaderUsername " +
                     "AND o.creadoEn >= :startDate " +
                     "AND o.creadoEn <= :endDate")
       Long getCollaboratorTotalProductsSold(@Param("uploaderUsername") String uploaderUsername,
                     @Param("startDate") LocalDateTime startDate,
                     @Param("endDate") LocalDateTime endDate);

       // Verificar si el colaborador existe (SQL Nativo - usa tabla 'users'
       // directamente)
       @Query(value = "SELECT COUNT(u.id) > 0 FROM users u WHERE u.nombre_usuario = :username AND u.rol = 'ROL_COLABORADOR'", nativeQuery = true)
       boolean existsCollaboratorByUsername(@Param("username") String username);

       // --- Nuevas Consultas para el Panel de Administrador ---

       // Total de colaboradores (SQL Nativo - usa tabla 'users' directamente)
       @Query(value = "SELECT COUNT(u.id) FROM users u WHERE u.rol = 'ROL_COLABORADOR'", nativeQuery = true)
       Long countTotalCollaborators();

       // Total de clientes (JPQL - solo Order, usando clienteUsername)
       @Query("SELECT COUNT(DISTINCT o.clienteUsername) FROM Order o WHERE o.status = 'COMPLETED'")
       Long countTotalCustomers();

       // Productos pendientes de revisión (JPQL - Product)
       @Query("SELECT COUNT(p.idProducto) FROM Product p WHERE p.estado = 'PENDIENTE'")
       Integer countProductsPendingReview();

       // Pagos pendientes de verificación (JPQL - Order)
       @Query("SELECT COUNT(o) FROM Order o WHERE o.paymentStatus = 'UPLOADED_RECEIPT'")
       Integer countPaymentsToVerify();

       // Errores en comprobantes de pago (JPQL - Order)
       @Query("SELECT COUNT(o) FROM Order o WHERE o.paymentStatus = 'PAYMENT_REJECTED'")
       Integer countPaymentErrors();

       // Total de productos registrados en el sistema (JPQL - Product)
       @Query("SELECT COUNT(p.idProducto) FROM Product p")
       Long countTotalProducts();

       // Ventas mensuales para el gráfico de línea (JPQL - solo Order)
       @Query("""
                     SELECT new com.aec.statssrv.dto.MonthlySalesDto(
                       MONTH(o.creadoEn),
                       SUM(o.total)
                     )
                     FROM Order o
                     WHERE o.status = 'COMPLETED'
                       AND YEAR(o.creadoEn) = :year
                     GROUP BY MONTH(o.creadoEn)
                     ORDER BY MONTH(o.creadoEn)
                     """)
       List<MonthlySalesDto> getMonthlySales(@Param("year") Integer year);

       // Obtener ingresos de un mes específico para el cálculo de crecimiento (JPQL -
       // solo Order)
       @Query("SELECT COALESCE(SUM(o.total), 0) FROM Order o " +
                     "WHERE o.status = 'COMPLETED' " +
                     "AND FUNCTION('YEAR', o.creadoEn) = :year " +
                     "AND FUNCTION('MONTH', o.creadoEn) = :month")
       BigDecimal getMonthlyRevenue(@Param("year") Integer year, @Param("month") Integer month);

       // Top productos vendidos en los últimos 30 días (JPQL - OrderItem y Product)
       @Query(value = "SELECT new com.aec.statssrv.dto.ProductSalesDto(" +
                     "p.idProducto, " +
                     "p.nombre, " +
                     "p.uploaderUsername, " +
                     "COALESCE(SUM(oi.precioUnitario * oi.cantidad), 0), " +
                     "COALESCE(SUM(oi.cantidad), 0), " +
                     "COUNT(DISTINCT o.id), " +
                     "p.precioIndividual, " +
                     "p.pais) " +
                     "FROM OrderItem oi " +
                     "JOIN oi.order o " +
                     "JOIN oi.product p " +
                     "WHERE o.status = 'COMPLETED' " +
                     "AND o.creadoEn >= :startDate " +
                     "AND o.creadoEn <= :endDate " +
                     "GROUP BY p.idProducto, p.nombre, p.uploaderUsername, p.precioIndividual, p.pais " +
                     "ORDER BY SUM(oi.precioUnitario * oi.cantidad) DESC", countQuery = "SELECT COUNT(DISTINCT p.idProducto) "
                                   + // CONTEO EXPLÍCITO
                                   "FROM OrderItem oi " +
                                   "JOIN oi.order o " +
                                   "JOIN oi.product p " +
                                   "WHERE o.status = 'COMPLETED' " +
                                   "AND o.creadoEn >= :startDate " +
                                   "AND o.creadoEn <= :endDate")
       List<ProductSalesDto> getTopProductsLast30Days(@Param("startDate") LocalDateTime startDate,
                     @Param("endDate") LocalDateTime endDate);

       // Ventas mensuales por colaborador para el gráfico de línea (JPQL - OrderItem y
       // Product)
       @Query("""
                     SELECT new com.aec.statssrv.dto.MonthlySalesDto(
                       MONTH(o.creadoEn),
                       SUM(oi.precioUnitario * oi.cantidad)
                     )
                     FROM OrderItem oi
                     JOIN oi.order o
                     JOIN oi.product p
                     WHERE o.status = 'COMPLETED'
                       AND p.uploaderUsername = :uploaderUsername
                       AND YEAR(o.creadoEn) = :year
                     GROUP BY MONTH(o.creadoEn)
                     ORDER BY MONTH(o.creadoEn)
                     """)
       List<MonthlySalesDto> getMonthlySalesByCollaborator(@Param("uploaderUsername") String uploaderUsername,
                     @Param("year") Integer year);
}
