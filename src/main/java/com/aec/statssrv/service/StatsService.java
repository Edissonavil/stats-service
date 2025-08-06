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

    public SalesStatsDto getAdminStats(StatsFilterDto filter) {
        LocalDateTime[] dateRange = getDateRange(filter);
        LocalDateTime startDate = dateRange[0];
        LocalDateTime endDate = dateRange[1];

        // Estadísticas generales del período filtrado
        BigDecimal totalRevenue = statsRepository.getTotalRevenue(startDate, endDate);
        Long totalOrders = statsRepository.getTotalCompletedOrders(startDate, endDate);
        Long totalProductsSold = statsRepository.getTotalProductsSold(startDate, endDate);

        SalesStatsDto stats = new SalesStatsDto(
                totalRevenue,
                totalOrders.intValue(),
                totalProductsSold.intValue());

        // --- Nuevas métricas para el Panel de Administrador (datos globales o del
        // sistema) ---
        // Ahora solo el total de colaboradores por rol
        stats.setTotalCollaborators(statsRepository.countTotalCollaborators());
        stats.setTotalCustomers(statsRepository.countTotalCustomers());
        stats.setProductsPendingReview(statsRepository.countProductsPendingReview());
        stats.setPaymentsToVerify(statsRepository.countPaymentsToVerify());
        stats.setPaymentErrors(statsRepository.countPaymentErrors());
        stats.setTotalProductsCount(statsRepository.countTotalProducts());

        // Cálculo del crecimiento mensual
        double monthlyGrowth = calculateMonthlyGrowth(filter.getYear(), filter.getMonth());
        stats.setMonthlyGrowthPercentage(monthlyGrowth);

        // Top productos vendidos (últimos 30 días - sin importar el filtro de año/mes)
        LocalDateTime last30DaysStart = LocalDateTime.now().minusDays(30);
        LocalDateTime now = LocalDateTime.now();
        stats.setTopProductsLast30Days(statsRepository.getTopProductsLast30Days(last30DaysStart, now));

        // Ventas por colaborador (para el período filtrado)
        List<CollaboratorSalesDto> collaboratorSales = statsRepository.getCollaboratorSales(startDate, endDate);
        stats.setCollaboratorSales(collaboratorSales);

        // Ventas por producto (para el período filtrado)
        List<ProductSalesDto> productSales = statsRepository.getProductSales(startDate, endDate, null);
        stats.setProductSales(productSales);

        // Estadísticas por método de pago (para el período filtrado)
        List<PaymentMethodStatsDto> paymentMethods = statsRepository.getPaymentMethodStats(startDate, endDate, null);
        calculatePaymentPercentages(paymentMethods, totalRevenue);
        stats.setPaymentMethods(paymentMethods);

        // Ventas mensuales para el gráfico de línea (siempre para el año completo del
        // filtro)
        List<MonthlySalesDto> monthlySales = statsRepository.getMonthlySales(filter.getYear());
        stats.setMonthlySales(monthlySales);

        return stats;
    }

    /**
     * Obtiene estadísticas para un colaborador específico
     */
    public SalesStatsDto getCollaboratorStats(String collaboratorUsername, StatsFilterDto filter) {
        if (!statsRepository.existsCollaboratorByUsername(collaboratorUsername)) {
            throw new CollaboratorNotFoundException("Colaborador no encontrado: " + collaboratorUsername);
        }

        LocalDateTime[] dateRange = getDateRange(filter);
        LocalDateTime startDate = dateRange[0];
        LocalDateTime endDate = dateRange[1];

        BigDecimal totalRevenue = statsRepository.getCollaboratorTotalRevenue(collaboratorUsername, startDate, endDate);
        Long totalOrders = statsRepository.getCollaboratorTotalOrders(collaboratorUsername, startDate, endDate);
        Long totalProducts = statsRepository.getCollaboratorTotalProductsSold(collaboratorUsername, startDate, endDate);

        SalesStatsDto stats = new SalesStatsDto(
                totalRevenue,
                totalOrders.intValue(),
                totalProducts.intValue());

        List<ProductSalesDto> productSales = statsRepository.getProductSales(startDate, endDate, collaboratorUsername);
        stats.setProductSales(productSales);

        List<PaymentMethodStatsDto> paymentMethods = statsRepository.getPaymentMethodStats(startDate, endDate,
                collaboratorUsername);
        calculatePaymentPercentages(paymentMethods, totalRevenue);
        stats.setPaymentMethods(paymentMethods);

        List<MonthlySalesDto> monthlySales = statsRepository.getMonthlySalesByCollaborator(collaboratorUsername,
                filter.getYear());
        stats.setMonthlySales(monthlySales);

        return stats;
    }

    public List<CollaboratorSalesDto> getCollaboratorSalesOnly(StatsFilterDto filter) {
        LocalDateTime[] dateRange = getDateRange(filter);
        return statsRepository.getCollaboratorSales(dateRange[0], dateRange[1]);
    }

    public List<ProductSalesDto> getProductSalesOnly(StatsFilterDto filter, String collaboratorUsername) {
        LocalDateTime[] dateRange = getDateRange(filter);
        return statsRepository.getProductSales(dateRange[0], dateRange[1], collaboratorUsername);
    }

    private LocalDateTime[] getDateRange(StatsFilterDto filter) {
        LocalDateTime startDate;
        LocalDateTime endDate;

        if (filter.getMonth() != null) {
            YearMonth yearMonth = YearMonth.of(filter.getYear(), filter.getMonth());
            startDate = yearMonth.atDay(1).atStartOfDay();
            endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59);
        } else {
            startDate = LocalDateTime.of(filter.getYear(), 1, 1, 0, 0, 0);
            endDate = LocalDateTime.of(filter.getYear(), 12, 31, 23, 59, 59);
        }

        return new LocalDateTime[] { startDate, endDate };
    }

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

    private double calculateMonthlyGrowth(Integer year, Integer currentMonthFilter) {
        BigDecimal currentPeriodRevenue;
        BigDecimal previousPeriodRevenue;

        if (currentMonthFilter != null) {
            currentPeriodRevenue = statsRepository.getMonthlyRevenue(year, currentMonthFilter);

            Integer prevMonthNum = currentMonthFilter - 1;
            Integer prevYearNum = year;
            if (prevMonthNum == 0) {
                prevMonthNum = 12;
                prevYearNum = year - 1;
            }
            previousPeriodRevenue = statsRepository.getMonthlyRevenue(prevYearNum, prevMonthNum);

        } else {
            // Si no se filtra por mes, tomamos el mes actual para el cálculo de crecimiento
            int currentMonth = LocalDateTime.now().getMonthValue();
            currentPeriodRevenue = statsRepository.getMonthlyRevenue(year, currentMonth);

            int prevMonth = currentMonth - 1;
            int prevYear = year;
            if (prevMonth == 0) {
                prevMonth = 12;
                prevYear = year - 1;
            }
            previousPeriodRevenue = statsRepository.getMonthlyRevenue(prevYear, prevMonth);
        }

        if (previousPeriodRevenue != null && previousPeriodRevenue.compareTo(BigDecimal.ZERO) > 0) {
            return currentPeriodRevenue.subtract(previousPeriodRevenue)
                    .divide(previousPeriodRevenue, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }
        return 0.0;
    }

    public boolean collaboratorExists(String username) {
        return statsRepository.existsCollaboratorByUsername(username);
    }

}

   