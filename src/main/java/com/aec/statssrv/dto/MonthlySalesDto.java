package com.aec.statssrv.dto;

import java.math.BigDecimal;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

public class MonthlySalesDto {
    private String month;
    private BigDecimal revenue;

    // CONSTRUCTOR 1: para cuando Hibernate devuelve un Double
    public MonthlySalesDto(Integer monthNumber, Double revenue) {
        this.revenue = (revenue != null) ? BigDecimal.valueOf(revenue) : BigDecimal.ZERO;
        this.month = Month.of(monthNumber).getDisplayName(TextStyle.SHORT, Locale.getDefault());
    }

    // CONSTRUCTOR 2: para cuando Hibernate devuelve un BigDecimal (más probable)
    public MonthlySalesDto(Integer monthNumber, BigDecimal revenue) {
        this.revenue = (revenue != null) ? revenue : BigDecimal.ZERO;
        this.month = Month.of(monthNumber).getDisplayName(TextStyle.SHORT, Locale.getDefault());
    }

    // Constructor para SQL nativo (sin cambios)
    public MonthlySalesDto(String month, BigDecimal revenue) {
        this.month = month;
        this.revenue = revenue;
    }

    // Getters y Setters
    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }
    public BigDecimal getRevenue() { return revenue; }
    public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
}