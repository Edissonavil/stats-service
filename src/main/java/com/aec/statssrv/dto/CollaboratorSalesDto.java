package com.aec.statssrv.dto;

import java.math.BigDecimal;

public class CollaboratorSalesDto {
    private String collaboratorUsername;
    private BigDecimal totalSales;
    private Integer totalQuantity;
    private Integer ordersCount;
    private String country;

    public CollaboratorSalesDto() {}

    // Constructor ajustado: 'uploaderUsername' coincide con el alias de la consulta nativa
    public CollaboratorSalesDto(String uploaderUsername, BigDecimal totalSales,
                               Long totalQuantity, Long ordersCount, String country) {
        this.collaboratorUsername = uploaderUsername; // Asignar el alias de la consulta al campo del DTO
        this.totalSales = totalSales;
        this.totalQuantity = totalQuantity != null ? totalQuantity.intValue() : 0;
        this.ordersCount = ordersCount != null ? ordersCount.intValue() : 0;
        this.country = country;
    }

    // Getters and setters
    public String getCollaboratorUsername() { return collaboratorUsername; }
    public void setCollaboratorUsername(String collaboratorUsername) {
        this.collaboratorUsername = collaboratorUsername;
    }

    public BigDecimal getTotalSales() { return totalSales; }
    public void setTotalSales(BigDecimal totalSales) { this.totalSales = totalSales; }

    public Integer getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(Integer totalQuantity) { this.totalQuantity = totalQuantity; }

    public Integer getOrdersCount() { return ordersCount; }
    public void setOrdersCount(Integer ordersCount) { this.ordersCount = ordersCount; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
}
