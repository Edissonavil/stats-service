package com.aec.statssrv.dto;

import java.math.BigDecimal;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

public class MonthlySalesDto {
    private String month;
    private BigDecimal revenue;

    /** Constructor para JPQL: recibe int + BigDecimal */
    public MonthlySalesDto(int monthNumber, BigDecimal revenue) {
        this.month   = Month.of(monthNumber)
                            .getDisplayName(TextStyle.SHORT, Locale.getDefault());
        this.revenue = revenue;
    }

    /** (Opcional) Otra firma si la usas en SQL nativo */
    public MonthlySalesDto(String month, BigDecimal revenue) {
        this.month   = month;
        this.revenue = revenue;
    }

    // getters / setters…
    public String getMonth()     { return month;   }
    public BigDecimal getRevenue(){ return revenue; }
    public void setMonth(String month)         { this.month = month;     }
    public void setRevenue(BigDecimal revenue){ this.revenue = revenue; }
}
