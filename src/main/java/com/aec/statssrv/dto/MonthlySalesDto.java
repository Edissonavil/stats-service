package com.aec.statssrv.dto;

import java.math.BigDecimal;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

public class MonthlySalesDto {
    private String month;
    private BigDecimal revenue;

    public MonthlySalesDto(int monthNumber, Number revenue) {
        this.month   = Month.of(monthNumber)
                            .getDisplayName(TextStyle.SHORT, Locale.getDefault());
        this.revenue = (revenue == null)
                       ? BigDecimal.ZERO
                       : (revenue instanceof BigDecimal
                          ? (BigDecimal) revenue
                          : BigDecimal.valueOf(revenue.doubleValue()));
    }

    // El constructor String+BigDecimal para tu SQL nativo
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