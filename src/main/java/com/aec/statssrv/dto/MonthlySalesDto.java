package com.aec.statssrv.dto;

import java.math.BigDecimal;

public class MonthlySalesDto {
    private String month;
    private BigDecimal revenue;

    // Nuevo constructor que coincide con los tipos devueltos por la consulta
    public MonthlySalesDto(Integer monthNumber, BigDecimal revenue) {
        // Convierte el número del mes a su nombre corto
        this.month = java.time.Month.of(monthNumber)
                .getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.getDefault());
        this.revenue = revenue;
    }

    // Constructor para el alias 'month' en SQL nativo (si aún lo necesitas)
    public MonthlySalesDto(String month, BigDecimal revenue) {
        this.month = month;
        this.revenue = revenue;
    }

    // Getters y Setters (sin cambios)
    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }
    public BigDecimal getRevenue() { return revenue; }
    public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
}