// src/main/java/com/aec/statssrv/dto/MonthlySalesDto.java
package com.aec.statssrv.dto;

import java.math.BigDecimal;

public class MonthlySalesDto {
    private String month;
    private BigDecimal revenue;

    public MonthlySalesDto(String month, BigDecimal revenue) {
        this.month = month;
        this.revenue = revenue;
    }

    // Constructor para recibir el número del mes y convertirlo
    public MonthlySalesDto(Integer monthNumber, BigDecimal revenue) {
        this.month = java.time.Month.of(monthNumber).getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.getDefault());
        this.revenue = revenue;
    }

    // Getters y Setters
    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }
    public BigDecimal getRevenue() { return revenue; }
    public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
}