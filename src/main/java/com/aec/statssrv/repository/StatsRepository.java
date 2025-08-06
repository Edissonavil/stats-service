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

    // --- Consultas existentes 
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

    @Query("SELECT COUNT(DISTINCT oi.productId) FROM OrderItem oi " +
            "JOIN oi.order o " +
            "WHERE o.status = 'COMPLETED' " +
            "AND o.creadoEn >= :startDate " +
            "AND o.creadoEn <= :endDate")
    Long getTotalProductsSold(@Param("startDate") LocalDateTime startDate,
                              @Param("endDate") LocalDateTime endDate);

    @Query("SELECT new com.aec.statssrv.dto.CollaboratorSalesDto(" +
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
            "ORDER BY SUM(oi.precioUnitario * oi.cantidad) DESC")
    List<CollaboratorSalesDto> getCollaboratorSales(@Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate);

    @Query("SELECT new com.aec.statssrv.dto.ProductSalesDto(" +
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
            "ORDER BY SUM(oi.precioUnitario * oi.cantidad) DESC")
    List<ProductSalesDto> getProductSales(@Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate,
                                          @Param("uploaderUsername") String uploaderUsername);

    @Query("SELECT new com.aec.statssrv.dto.PaymentMethodStatsDto(" +
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
            "ORDER BY SUM(oi.precioUnitario * oi.cantidad) DESC")
    List<PaymentMethodStatsDto> getPaymentMethodStats(@Param("startDate") LocalDateTime startDate,
                                                      @Param("endDate") LocalDateTime endDate,
                                                      @Param("uploaderUsername") String uploaderUsername);

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

    @Query("SELECT COUNT(p) > 0 FROM Product p WHERE p.uploaderUsername = :username")
    boolean existsCollaboratorByUsername(@Param("username") String username);

    // --- Nuevas Consultas ---

    // Total de colaboradores (basado en el rol, ya que no hay 'status')
    @Query("SELECT COUNT(u) FROM User u WHERE u.rol = 'ROL_COLABORADOR'")
    Long countTotalCollaborators();

    // Total de clientes (usuarios que han realizado al menos una compra completada)
    @Query("SELECT COUNT(DISTINCT o.clienteId) FROM Order o WHERE o.status = 'COMPLETED'")
    Long countTotalCustomers();

    // Productos pendientes de revisión (asumiendo entidad Product con status)
    @Query("SELECT COUNT(p) FROM Product p WHERE p.status = 'PENDIENTE_REVISION'")
    Integer countProductsPendingReview();

    // Pagos pendientes de verificación (asumiendo entidad Order o Payment con status)
    @Query("SELECT COUNT(o) FROM Order o WHERE o.paymentStatus = 'PENDIENTE_VERIFICACION'")
    Integer countPaymentsToVerify();

    // Errores en comprobantes de pago
    @Query("SELECT COUNT(o) FROM Order o WHERE o.paymentStatus = 'ERROR_VERIFICACION'")
    Integer countPaymentErrors();

    // Total de productos registrados en el sistema
    @Query("SELECT COUNT(p) FROM Product p")
    Long countTotalProducts();

    // Ventas mensuales para el gráfico de línea (para el año seleccionado)
    @Query("SELECT new com.aec.statssrv.dto.MonthlySalesDto(FUNCTION('MONTH', o.creadoEn), COALESCE(SUM(o.total), 0)) " +
            "FROM Order o " +
            "WHERE o.status = 'COMPLETED' " +
            "AND FUNCTION('YEAR', o.creadoEn) = :year " +
            "GROUP BY FUNCTION('MONTH', o.creadoEn) " +
            "ORDER BY FUNCTION('MONTH', o.creadoEn)")
    List<MonthlySalesDto> getMonthlySales(@Param("year") Integer year);

    // Obtener ingresos de un mes específico para el cálculo de crecimiento
    @Query("SELECT COALESCE(SUM(o.total), 0) FROM Order o " +
            "WHERE o.status = 'COMPLETED' " +
            "AND FUNCTION('YEAR', o.creadoEn) = :year " +
            "AND FUNCTION('MONTH', o.creadoEn) = :month")
    BigDecimal getMonthlyRevenue(@Param("year") Integer year, @Param("month") Integer month);


    // Top productos vendidos en los últimos 30 días
    @Query("SELECT new com.aec.statssrv.dto.ProductSalesDto(" +
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
            "ORDER BY SUM(oi.precioUnitario * oi.cantidad) DESC")
    List<ProductSalesDto> getTopProductsLast30Days(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Ventas mensuales por colaborador para el gráfico de línea (si se requiere)
    @Query("SELECT new com.aec.statssrv.dto.MonthlySalesDto(FUNCTION('MONTH', o.creadoEn), COALESCE(SUM(oi.precioUnitario * oi.cantidad), 0)) " +
            "FROM OrderItem oi " +
            "JOIN oi.order o " +
            "JOIN oi.product p " +
            "WHERE o.status = 'COMPLETED' " +
            "AND p.uploaderUsername = :uploaderUsername " +
            "AND FUNCTION('YEAR', o.creadoEn) = :year " +
            "GROUP BY FUNCTION('MONTH', o.creadoEn) " +
            "ORDER BY FUNCTION('MONTH', o.creadoEn)")
    List<MonthlySalesDto> getMonthlySalesByCollaborator(@Param("uploaderUsername") String uploaderUsername, @Param("year") Integer year);
}