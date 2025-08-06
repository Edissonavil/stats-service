// src/main/java/com/aec/statssrv/dto/MonthlySalesDto.java
package com.aec.statssrv.dto;

import java.math.BigDecimal;

public class MonthlySalesDto {
    private String month;
    private BigDecimal revenue;

    // Este constructor es para la consulta JPQL (FUNCTION('MONTH', o.creadoEn) que devuelve Integer)
    public MonthlySalesDto(Integer monthNumber, BigDecimal revenue) {
        // Convierte el número del mes a su nombre corto (ej. "Ene", "Feb")
        this.month = java.time.Month.of(monthNumber).getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.getDefault());
        this.revenue = revenue;
    }

   
    public MonthlySalesDto(String month, BigDecimal revenue) { // Constructor para el alias 'month' en SQL nativo
        this.month = month;
        this.revenue = revenue;
    }


    // Getters y Setters
    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }
    public BigDecimal getRevenue() { return revenue; }
    public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
}
