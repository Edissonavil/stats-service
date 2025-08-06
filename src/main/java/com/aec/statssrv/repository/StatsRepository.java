// src/main/java/com/aec/statssrv/repository/StatsRepository.java
package com.aec.statssrv.repository;

import com.aec.statssrv.dto.*;
import com.aec.statssrv.model.Order;
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

    /* ----------  MÉTRICAS GENERALES  ---------- */

    @Query("""
            SELECT COALESCE(SUM(o.total), 0)
            FROM   Order o
            WHERE  o.status = 'COMPLETED'
              AND  o.creadoEn >= :start
              AND  o.creadoEn <  :end
           """)
    BigDecimal getTotalRevenue(@Param("start") LocalDateTime start,
                               @Param("end")   LocalDateTime end);

    @Query("""
            SELECT COUNT(o)
            FROM   Order o
            WHERE  o.status = 'COMPLETED'
              AND  o.creadoEn >= :start
              AND  o.creadoEn <  :end
           """)
    Long getTotalCompletedOrders(@Param("start") LocalDateTime start,
                                 @Param("end")   LocalDateTime end);

    @Query("""
            SELECT COUNT(DISTINCT oi.productId)
            FROM   OrderItem oi
            JOIN   oi.order o
            WHERE  o.status = 'COMPLETED'
              AND  o.creadoEn >= :start
              AND  o.creadoEn <  :end
           """)
    Long getTotalProductsSold(@Param("start") LocalDateTime start,
                              @Param("end")   LocalDateTime end);

    /* ----------  VENTAS POR COLABORADOR / PRODUCTO  ---------- */

    @Query("""
            SELECT new com.aec.statssrv.dto.CollaboratorSalesDto(
                p.uploaderUsername,
                SUM(oi.precioUnitario * oi.cantidad),
                SUM(oi.cantidad),
                COUNT(DISTINCT o.id),
                p.pais
            )
            FROM   OrderItem oi
            JOIN   oi.order  o
            JOIN   oi.product p
            WHERE  o.status = 'COMPLETED'
              AND  o.creadoEn >= :start
              AND  o.creadoEn <  :end
            GROUP  BY p.uploaderUsername, p.pais
            ORDER  BY SUM(oi.precioUnitario * oi.cantidad) DESC
           """)
    List<CollaboratorSalesDto> getCollaboratorSales(@Param("start") LocalDateTime start,
                                                    @Param("end")   LocalDateTime end);

    @Query("""
            SELECT new com.aec.statssrv.dto.ProductSalesDto(
                p.idProducto,
                p.nombre,
                p.uploaderUsername,
                SUM(oi.precioUnitario * oi.cantidad),
                SUM(oi.cantidad),
                COUNT(DISTINCT o.id),
                p.precioIndividual,
                p.pais
            )
            FROM   OrderItem oi
            JOIN   oi.order  o
            JOIN   oi.product p
            WHERE  o.status = 'COMPLETED'
              AND  o.creadoEn >= :start
              AND  o.creadoEn <  :end
              AND  (:uploaderUsername IS NULL OR p.uploaderUsername = :uploaderUsername)
            GROUP  BY p.idProducto, p.nombre, p.uploaderUsername, p.precioIndividual, p.pais
            ORDER  BY SUM(oi.precioUnitario * oi.cantidad) DESC
           """)
    List<ProductSalesDto> getProductSales(@Param("start")           LocalDateTime start,
                                          @Param("end")             LocalDateTime end,
                                          @Param("uploaderUsername") String uploaderUsername);

    /* ----------  MÉTODOS DE PAGO  ---------- */

    @Query("""
            SELECT new com.aec.statssrv.dto.PaymentMethodStatsDto(
                COALESCE(o.paymentMethod, 'NO_ESPECIFICADO'),
                SUM(oi.precioUnitario * oi.cantidad),
                COUNT(DISTINCT o.id)
            )
            FROM   OrderItem oi
            JOIN   oi.order  o
            JOIN   oi.product p
            WHERE  o.status = 'COMPLETED'
              AND  o.creadoEn >= :start
              AND  o.creadoEn <  :end
              AND  (:uploaderUsername IS NULL OR p.uploaderUsername = :uploaderUsername)
            GROUP  BY o.paymentMethod
            ORDER  BY SUM(oi.precioUnitario * oi.cantidad) DESC
           """)
    List<PaymentMethodStatsDto> getPaymentMethodStats(@Param("start")           LocalDateTime start,
                                                      @Param("end")             LocalDateTime end,
                                                      @Param("uploaderUsername") String uploaderUsername);

    /* ----------  ESTADÍSTICAS DE COLABORADOR  ---------- */

    @Query("""
            SELECT SUM(oi.precioUnitario * oi.cantidad)
            FROM   OrderItem oi
            JOIN   oi.order  o
            JOIN   oi.product p
            WHERE  o.status = 'COMPLETED'
              AND  p.uploaderUsername = :uploaderUsername
              AND  o.creadoEn >= :start
              AND  o.creadoEn <  :end
           """)
    BigDecimal getCollaboratorTotalRevenue(@Param("uploaderUsername") String uploaderUsername,
                                           @Param("start")           LocalDateTime start,
                                           @Param("end")             LocalDateTime end);

    @Query("""
            SELECT COUNT(DISTINCT o.id)
            FROM   OrderItem oi
            JOIN   oi.order  o
            JOIN   oi.product p
            WHERE  o.status = 'COMPLETED'
              AND  p.uploaderUsername = :uploaderUsername
              AND  o.creadoEn >= :start
              AND  o.creadoEn <  :end
           """)
    Long getCollaboratorTotalOrders(@Param("uploaderUsername") String uploaderUsername,
                                    @Param("start")           LocalDateTime start,
                                    @Param("end")             LocalDateTime end);

    @Query("""
            SELECT COUNT(DISTINCT oi.productId)
            FROM   OrderItem oi
            JOIN   oi.order  o
            JOIN   oi.product p
            WHERE  o.status = 'COMPLETED'
              AND  p.uploaderUsername = :uploaderUsername
              AND  o.creadoEn >= :start
              AND  o.creadoEn <  :end
           """)
    Long getCollaboratorTotalProductsSold(@Param("uploaderUsername") String uploaderUsername,
                                          @Param("start")           LocalDateTime start,
                                          @Param("end")             LocalDateTime end);

    /* ----------  PANEL ADMIN  ---------- */

    @Query(value = "SELECT COUNT(u.id) FROM users u WHERE u.rol = 'ROL_COLABORADOR'", nativeQuery = true)
    Long countTotalCollaborators();

    @Query("SELECT COUNT(DISTINCT o.clienteUsername) FROM Order o WHERE o.status = 'COMPLETED'")
    Long countTotalCustomers();

    @Query("SELECT COUNT(p.idProducto) FROM Product p WHERE p.estado = 'PENDIENTE'")
    Integer countProductsPendingReview();

    @Query("SELECT COUNT(o) FROM Order o WHERE o.paymentStatus = 'UPLOADED_RECEIPT'")
    Integer countPaymentsToVerify();

    @Query("SELECT COUNT(o) FROM Order o WHERE o.paymentStatus = 'PAYMENT_REJECTED'")
    Integer countPaymentErrors();

    @Query("SELECT COUNT(p.idProducto) FROM Product p")
    Long countTotalProducts();

    /* ----------  VENTAS MENSUALES (GENERAL)  ---------- */
    @Query("""
            SELECT new com.aec.statssrv.dto.MonthlySalesDto(
                MONTH(o.creadoEn),
                SUM(o.total)
            )
            FROM   Order o
            WHERE  o.status = 'COMPLETED'
              AND  o.creadoEn >= :start
              AND  o.creadoEn <  :end
            GROUP  BY MONTH(o.creadoEn)
            ORDER  BY MONTH(o.creadoEn)
           """)
    List<MonthlySalesDto> getMonthlySales(@Param("start") LocalDateTime startOfYear,
                                          @Param("end")   LocalDateTime endOfYearExclusive);

    /* ----------  VENTAS MENSUALES POR COLABORADOR  ---------- */
    @Query("""
            SELECT new com.aec.statssrv.dto.MonthlySalesDto(
                MONTH(o.creadoEn),
                SUM(oi.precioUnitario * oi.cantidad)
            )
            FROM   OrderItem oi
            JOIN   oi.order  o
            JOIN   oi.product p
            WHERE  o.status = 'COMPLETED'
              AND  p.uploaderUsername = :uploaderUsername
              AND  o.creadoEn >= :start
              AND  o.creadoEn <  :end
            GROUP  BY MONTH(o.creadoEn)
            ORDER  BY MONTH(o.creadoEn)
           """)
    List<MonthlySalesDto> getMonthlySalesByCollaborator(@Param("uploaderUsername") String uploaderUsername,
                                                        @Param("start")           LocalDateTime startOfYear,
                                                        @Param("end")             LocalDateTime endOfYearExclusive);

    /* ----------  TOP PRODUCTOS ÚLTIMOS 30 DÍAS  ---------- */
    @Query("""
            SELECT new com.aec.statssrv.dto.ProductSalesDto(
                p.idProducto,
                p.nombre,
                p.uploaderUsername,
                SUM(oi.precioUnitario * oi.cantidad),
                SUM(oi.cantidad),
                COUNT(DISTINCT o.id),
                p.precioIndividual,
                p.pais
            )
            FROM   OrderItem oi
            JOIN   oi.order  o
            JOIN   oi.product p
            WHERE  o.status = 'COMPLETED'
              AND  o.creadoEn >= :start
              AND  o.creadoEn <  :end
            GROUP  BY p.idProducto, p.nombre, p.uploaderUsername, p.precioIndividual, p.pais
            ORDER  BY SUM(oi.precioUnitario * oi.cantidad) DESC
           """)
    List<ProductSalesDto> getTopProductsLast30Days(@Param("start") LocalDateTime start,
                                                   @Param("end")   LocalDateTime end);

    /* ----------  UTILIDAD: VERIFICAR COLABORADOR EXISTE  ---------- */
    @Query(value = """
            SELECT COUNT(u.id) > 0
            FROM   users u
            WHERE  u.nombre_usuario = :username
              AND  u.rol            = 'ROL_COLABORADOR'
           """, nativeQuery = true)
    boolean existsCollaboratorByUsername(@Param("username") String username);
}

