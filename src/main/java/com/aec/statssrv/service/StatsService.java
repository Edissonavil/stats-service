// src/main/java/com/aec/statssrv/service/StatsService.java
package com.aec.statssrv.service;


import com.aec.statssrv.dto.*;
import com.aec.statssrv.exception.CollaboratorNotFoundException;
import com.aec.statssrv.repository.StatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
public class StatsService {
    
    @Autowired
    private StatsRepository statsRepository;
    
    /**
     * Obtiene estadísticas completas para administradores
     */
    public SalesStatsDto getAdminStats(StatsFilterDto filter) {
        LocalDateTime[] dateRange = getDateRange(filter);
        LocalDateTime startDate = dateRange[0];
        LocalDateTime endDate = dateRange[1];
        
        // Estadísticas generales
        BigDecimal totalRevenue = statsRepository.getTotalRevenue(startDate, endDate);
        Long totalOrders = statsRepository.getTotalCompletedOrders(startDate, endDate);
        Long totalProducts = statsRepository.getTotalProductsSold(startDate, endDate);
        
        // Crear objeto de respuesta
        SalesStatsDto stats = new SalesStatsDto(
            totalRevenue,
            totalOrders.intValue(),
            totalProducts.intValue()
        );
        
        // Ventas por colaborador
        List<CollaboratorSalesDto> collaboratorSales = statsRepository.getCollaboratorSales(startDate, endDate);
        stats.setCollaboratorSales(collaboratorSales);
        
        // Ventas por producto
        List<ProductSalesDto> productSales = statsRepository.getProductSales(startDate, endDate, null);
        stats.setProductSales(productSales);
        
        // Estadísticas por método de pago
        List<PaymentMethodStatsDto> paymentMethods = statsRepository.getPaymentMethodStats(startDate, endDate, null);
        calculatePaymentPercentages(paymentMethods, totalRevenue);
        stats.setPaymentMethods(paymentMethods);
        
        return stats;
    }
    
    /**
     * Obtiene estadísticas para un colaborador específico
     */
    public SalesStatsDto getCollaboratorStats(String collaboratorUsername, StatsFilterDto filter) {
        // Verificar que el colaborador existe
        if (!statsRepository.existsCollaboratorByUsername(collaboratorUsername)) {
            throw new CollaboratorNotFoundException("Colaborador no encontrado: " + collaboratorUsername);
        }
        
        LocalDateTime[] dateRange = getDateRange(filter);
        LocalDateTime startDate = dateRange[0];
        LocalDateTime endDate = dateRange[1];
        
        // Estadísticas específicas del colaborador
        BigDecimal totalRevenue = statsRepository.getCollaboratorTotalRevenue(collaboratorUsername, startDate, endDate);
        Long totalOrders = statsRepository.getCollaboratorTotalOrders(collaboratorUsername, startDate, endDate);
        Long totalProducts = statsRepository.getCollaboratorTotalProductsSold(collaboratorUsername, startDate, endDate);
        
        SalesStatsDto stats = new SalesStatsDto(
            totalRevenue,
            totalOrders.intValue(),
            totalProducts.intValue()
        );
        
        // Solo productos del colaborador
        List<ProductSalesDto> productSales = statsRepository.getProductSales(startDate, endDate, collaboratorUsername);
        stats.setProductSales(productSales);
        
        // Métodos de pago del colaborador
        List<PaymentMethodStatsDto> paymentMethods = statsRepository.getPaymentMethodStats(startDate, endDate, collaboratorUsername);
        calculatePaymentPercentages(paymentMethods, totalRevenue);
        stats.setPaymentMethods(paymentMethods);
        
        return stats;
    }
    
    /**
     * Obtiene solo las ventas por colaborador (para vista rápida de admin)
     */
    public List<CollaboratorSalesDto> getCollaboratorSalesOnly(StatsFilterDto filter) {
        LocalDateTime[] dateRange = getDateRange(filter);
        return statsRepository.getCollaboratorSales(dateRange[0], dateRange[1]);
    }
    
    /**
     * Obtiene solo las ventas por producto
     */
    public List<ProductSalesDto> getProductSalesOnly(StatsFilterDto filter, String collaboratorUsername) {
        LocalDateTime[] dateRange = getDateRange(filter);
        return statsRepository.getProductSales(dateRange[0], dateRange[1], collaboratorUsername);
    }
    
    /**
     * Calcula el rango de fechas basado en el filtro
     */
    private LocalDateTime[] getDateRange(StatsFilterDto filter) {
        LocalDateTime startDate;
        LocalDateTime endDate;
        
        if (filter.getMonth() != null) {
            // Filtro por mes específico
            YearMonth yearMonth = YearMonth.of(filter.getYear(), filter.getMonth());
            startDate = yearMonth.atDay(1).atStartOfDay();
            endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59);
        } else {
            // Filtro por año completo
            startDate = LocalDateTime.of(filter.getYear(), 1, 1, 0, 0, 0);
            endDate = LocalDateTime.of(filter.getYear(), 12, 31, 23, 59, 59);
        }
        
        return new LocalDateTime[]{startDate, endDate};
    }
    
    /**
     * Calcula los porcentajes para cada método de pago
     */
    private void calculatePaymentPercentages(List<PaymentMethodStatsDto> paymentMethods, BigDecimal totalRevenue) {
        if (totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
            paymentMethods.forEach(pm -> {
                BigDecimal percentage = pm.getTotalAmount()
                    .divide(totalRevenue, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
                pm.setPercentage(percentage.doubleValue());
            });
        }
    }
    
    /**
     * Verifica si un colaborador existe
     */
    public boolean collaboratorExists(String username) {
        return statsRepository.existsCollaboratorByUsername(username);
    }
}
