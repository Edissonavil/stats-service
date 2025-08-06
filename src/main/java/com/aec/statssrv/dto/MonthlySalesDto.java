package com.aec.statssrv.dto;

import java.math.BigDecimal;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

public class MonthlySalesDto {
    private String month;
    private BigDecimal revenue;

     /** Hibernate necesita esta firma exacta (int, BigDecimal) */
    public MonthlySalesDto(int monthNumber, BigDecimal revenue) {
        this.month   = Month.of(monthNumber)
                            .getDisplayName(TextStyle.SHORT, Locale.getDefault());
        this.revenue = (revenue != null) ? revenue : BigDecimal.ZERO;
    }

    // (Opcional) el que usas para SQL nativo o string-based:
    public MonthlySalesDto(String month, BigDecimal revenue) {
        this.month   = month;
        this.revenue = (revenue != null) ? revenue : BigDecimal.ZERO;
    }
    

    // Getters y Setters
    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }
}