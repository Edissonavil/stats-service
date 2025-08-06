// src/main/java/com/aec/statssrv/service/StatsService.java
package com.aec.statssrv.service;

import com.aec.statssrv.dto.*;
import com.aec.statssrv.exception.CollaboratorNotFoundException;
import com.aec.statssrv.repository.StatsRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
public class StatsService {

    private final StatsRepository statsRepository;

    public StatsService(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    /* -------------------------------------------------------------------- */
    /* ======================  PANEL ADMIN GENERAL  ======================= */
    /* -------------------------------------------------------------------- */

    public SalesStatsDto getAdminStats(StatsFilterDto filter) {

        /* rango principal según el filtro                (>= start  &&  < end) */
        LocalDateTime[] range       = resolveRange(filter);
        LocalDateTime    start      = range[0];
        LocalDateTime    end        = range[1];

        /* métricas globales */
        BigDecimal totalRevenue     = statsRepository.getTotalRevenue(start, end);
        long       totalOrders      = statsRepository.getTotalCompletedOrders(start, end);
        long       totalProdSold    = statsRepository.getTotalProductsSold(start, end);

        SalesStatsDto stats = new SalesStatsDto(
                totalRevenue,
                (int) totalOrders,
                (int) totalProdSold);

        /* dashboard extra  */
        stats.setTotalCollaborators(statsRepository.countTotalCollaborators());
        stats.setTotalCustomers    (statsRepository.countTotalCustomers());
        stats.setProductsPendingReview(statsRepository.countProductsPendingReview());
        stats.setPaymentsToVerify     (statsRepository.countPaymentsToVerify());
        stats.setPaymentErrors        (statsRepository.countPaymentErrors());
        stats.setTotalProductsCount   (statsRepository.countTotalProducts());

        /* crecimiento inter-mensual  */
        stats.setMonthlyGrowthPercentage(
                calcGrowthPercentage(filter.getYear(), filter.getMonth())
        );

        /* top productos últimos 30 d */
        LocalDateTime now = LocalDateTime.now();
        stats.setTopProductsLast30Days(
                statsRepository.getTopProductsLast30Days(now.minusDays(30), now)
        );

        /* ventas por colaborador / producto / método de pago */
        stats.setCollaboratorSales(statsRepository.getCollaboratorSales(start, end));
        stats.setProductSales      (statsRepository.getProductSales(start, end, null));

        List<PaymentMethodStatsDto> pm =
                statsRepository.getPaymentMethodStats(start, end, null);
        setPaymentPercentages(pm, totalRevenue);
        stats.setPaymentMethods(pm);

        /* ventas mensuales del año completo del filtro             */
        LocalDateTime yearStart = startOfYear(filter.getYear());
        LocalDateTime yearEnd   = yearStart.plusYears(1);
        stats.setMonthlySales(
                statsRepository.getMonthlySales(yearStart, yearEnd)
        );

        return stats;
    }

    /* -------------------------------------------------------------------- */
    /* ================  PANEL PARA COLABORADOR ESPECÍFICO  ================ */
    /* -------------------------------------------------------------------- */

    public SalesStatsDto getCollaboratorStats(String username, StatsFilterDto filter) {

        if (!statsRepository.existsCollaboratorByUsername(username)) {
            throw new CollaboratorNotFoundException("Colaborador no encontrado: " + username);
        }

        LocalDateTime[] range    = resolveRange(filter);
        LocalDateTime    start   = range[0];
        LocalDateTime    end     = range[1];

        BigDecimal revenue       = statsRepository.getCollaboratorTotalRevenue(username, start, end);
        long       orders        = statsRepository.getCollaboratorTotalOrders(username, start, end);
        long       productsSold  = statsRepository.getCollaboratorTotalProductsSold(username, start, end);

        SalesStatsDto stats = new SalesStatsDto(revenue, (int) orders, (int) productsSold);

        stats.setProductSales(
                statsRepository.getProductSales(start, end, username)
        );

        List<PaymentMethodStatsDto> pm =
                statsRepository.getPaymentMethodStats(start, end, username);
        setPaymentPercentages(pm, revenue);
        stats.setPaymentMethods(pm);

        /* serie mensual (año completo del filtro) */
        LocalDateTime yearStart = startOfYear(filter.getYear());
        LocalDateTime yearEnd   = yearStart.plusYears(1);
        stats.setMonthlySales(
                statsRepository.getMonthlySalesByCollaborator(username, yearStart, yearEnd)
        );

        return stats;
    }

    /* -------------------------------------------------------------------- */
    /* ======================  ENDPOINTS AUXILIARES  ====================== */
    /* -------------------------------------------------------------------- */

    public List<CollaboratorSalesDto> getCollaboratorSalesOnly(StatsFilterDto filter) {
        LocalDateTime[] r = resolveRange(filter);
        return statsRepository.getCollaboratorSales(r[0], r[1]);
    }

    public List<ProductSalesDto> getProductSalesOnly(StatsFilterDto filter, String uploader) {
        LocalDateTime[] r = resolveRange(filter);
        return statsRepository.getProductSales(r[0], r[1], uploader);
    }

    public boolean collaboratorExists(String username) {
        return statsRepository.existsCollaboratorByUsername(username);
    }

    /* -------------------------------------------------------------------- */
    /* =========================  MÉTODOS PRIVADOS  ======================== */
    /* -------------------------------------------------------------------- */

    /** Devuelve un rango [start, end) acorde al filtro */
    private LocalDateTime[] resolveRange(StatsFilterDto filter) {

        if (filter.getMonth() != null) {               // rango de un mes concreto
            YearMonth ym   = YearMonth.of(filter.getYear(), filter.getMonth());
            LocalDateTime start = ym.atDay(1).atStartOfDay();
            LocalDateTime end   = start.plusMonths(1); // exclusivo
            return new LocalDateTime[]{start, end};

        } else {                                       // rango del año completo
            LocalDateTime start = startOfYear(filter.getYear());
            return new LocalDateTime[]{start, start.plusYears(1)};
        }
    }

    private LocalDateTime startOfYear(int year) {
        return LocalDateTime.of(year, 1, 1, 0, 0);
    }

    /** Calcula % crecimiento entre mes actual y mes anterior. */
    private double calcGrowthPercentage(int year, Integer monthFilter) {

        /* Si el filtro tiene mes usamos ese; si no, usamos el mes actual */
        int currMonth = (monthFilter != null) ? monthFilter : LocalDateTime.now().getMonthValue();
        int currYear  = year;

        int prevMonth = currMonth - 1;
        int prevYear  = currYear;
        if (prevMonth == 0) { prevMonth = 12; prevYear--; }

        BigDecimal curr = revenueForMonth(currYear,  currMonth);
        BigDecimal prev = revenueForMonth(prevYear,  prevMonth);

        if (prev.compareTo(BigDecimal.ZERO) > 0) {
            return curr.subtract(prev)
                       .divide(prev, 4, RoundingMode.HALF_UP)
                       .multiply(BigDecimal.valueOf(100))
                       .doubleValue();
        }
        return 0.0;
    }

    /** Suma de ingresos de un mes (usa rango de fechas, no funciones SQL). */
    private BigDecimal revenueForMonth(int year, int month) {
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end   = start.plusMonths(1);
        return statsRepository.getTotalRevenue(start, end);
    }

    /** Completa el % del total para cada método de pago */
    private void setPaymentPercentages(List<PaymentMethodStatsDto> list, BigDecimal total) {
        if (total.compareTo(BigDecimal.ZERO) == 0) return;

        list.forEach(pm -> {
            BigDecimal pct = pm.getTotalAmount()
                               .divide(total, 4, RoundingMode.HALF_UP)
                               .multiply(BigDecimal.valueOf(100));
            pm.setPercentage(pct.doubleValue());
        });
    }
}
