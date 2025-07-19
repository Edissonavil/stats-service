package com.aec.statssrv.repository;


import com.aec.statssrv.dto.CollaboratorSalesDto;
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
    
    // Estadísticas generales para admin
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
    
    // Ventas por colaborador (para admin)
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
    
    // Ventas por producto (para admin o colaborador específico)
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
    
    // Estadísticas por método de pago (para admin o colaborador específico)
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
    
    // Estadísticas específicas para colaborador
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
    
    // Verificar si el usuario existe y tiene productos
    @Query("SELECT COUNT(p) > 0 FROM Product p WHERE p.uploaderUsername = :username")
    boolean existsCollaboratorByUsername(@Param("username") String username);
}