// src/main/java/com/aec/statssrv/dto/MonthlySalesDto.java
package com.aec.statssrv.dto;

import java.math.BigDecimal;

public class MonthlySalesDto {
    private String month;
    private BigDecimal revenue;

    // *** MODIFICAR ESTE CONSTRUCTOR ***
    public MonthlySalesDto(Integer monthNumber, Double revenue) {
        // Convierte el Double a BigDecimal si es necesario
        this.revenue = BigDecimal.valueOf(revenue);
        // Convierte el número del mes a su nombre corto (ej. "Ene", "Feb")
        this.month = java.time.Month.of(monthNumber).getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.getDefault());
    }

    // Constructor para el alias 'month' en SQL nativo (este estaba bien)
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