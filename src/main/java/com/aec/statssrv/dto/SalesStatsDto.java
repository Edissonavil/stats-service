// src/main/java/com/aec/statssrv/dto/SalesStatsDto.java
package com.aec.statssrv.dto;

import java.math.BigDecimal;
import java.util.List;

public class SalesStatsDto {
    private BigDecimal totalRevenue;
    private Integer totalOrders;
    private Integer totalProductsSold;
    private List<CollaboratorSalesDto> collaboratorSales;
    private List<ProductSalesDto> productSales;
    private List<PaymentMethodStatsDto> paymentMethods;
    private List<MonthlySalesDto> monthlySales;

    // Campos para el Panel de Administrador
    private Long totalCollaborators; // Ahora solo el total de colaboradores por rol
    private Long totalCustomers;
    private Integer productsPendingReview;
    private Integer paymentsToVerify;
    private Integer paymentErrors;
    private Long totalProductsCount;
    private Double monthlyGrowthPercentage;
    private List<ProductSalesDto> topProductsLast30Days;

    public SalesStatsDto(BigDecimal totalRevenue, Integer totalOrders, Integer totalProductsSold) {
        this.totalRevenue = totalRevenue;
        this.totalOrders = totalOrders;
        this.totalProductsSold = totalProductsSold;
    }

    public SalesStatsDto() {}

    // Getters y Setters para todos los campos (existentes y nuevos)
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
    public Integer getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Integer totalOrders) { this.totalOrders = totalOrders; }
    public Integer getTotalProductsSold() { return totalProductsSold; }
    public void setTotalProductsSold(Integer totalProductsSold) { this.totalProductsSold = totalProductsSold; }
    public List<CollaboratorSalesDto> getCollaboratorSales() { return collaboratorSales; }
    public void setCollaboratorSales(List<CollaboratorSalesDto> collaboratorSales) { this.collaboratorSales = collaboratorSales; }
    public List<ProductSalesDto> getProductSales() { return productSales; }
    public void setProductSales(List<ProductSalesDto> productSales) { this.productSales = productSales; }
    public List<PaymentMethodStatsDto> getPaymentMethods() { return paymentMethods; }
    public void setPaymentMethods(List<PaymentMethodStatsDto> paymentMethods) { this.paymentMethods = paymentMethods; }
    public List<MonthlySalesDto> getMonthlySales() { return monthlySales; }
    public void setMonthlySales(List<MonthlySalesDto> monthlySales) { this.monthlySales = monthlySales; }

    // Nuevos Getters y Setters (ajustados)
    public Long getTotalCollaborators() { return totalCollaborators; } // Ajustado
    public void setTotalCollaborators(Long totalCollaborators) { this.totalCollaborators = totalCollaborators; } // Ajustado
    public Long getTotalCustomers() { return totalCustomers; }
    public void setTotalCustomers(Long totalCustomers) { this.totalCustomers = totalCustomers; }
    public Integer getProductsPendingReview() { return productsPendingReview; }
    public void setProductsPendingReview(Integer productsPendingReview) { this.productsPendingReview = productsPendingReview; }
    public Integer getPaymentsToVerify() { return paymentsToVerify; }
    public void setPaymentsToVerify(Integer paymentsToVerify) { this.paymentsToVerify = paymentsToVerify; }
    public Integer getPaymentErrors() { return paymentErrors; }
    public void setPaymentErrors(Integer paymentErrors) { this.paymentErrors = paymentErrors; }
    public Long getTotalProductsCount() { return totalProductsCount; }
    public void setTotalProductsCount(Long totalProductsCount) { this.totalProductsCount = totalProductsCount; }
    public Double getMonthlyGrowthPercentage() { return monthlyGrowthPercentage; }
    public void setMonthlyGrowthPercentage(Double monthlyGrowthPercentage) { this.monthlyGrowthPercentage = monthlyGrowthPercentage; }
    public List<ProductSalesDto> getTopProductsLast30Days() { return topProductsLast30Days; }
    public void setTopProductsLast30Days(List<ProductSalesDto> topProductsLast30Days) { this.topProductsLast30Days = topProductsLast30Days; }
}