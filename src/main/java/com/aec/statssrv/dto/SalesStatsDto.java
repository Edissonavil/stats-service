package com.aec.statssrv.dto;

import java.math.BigDecimal;
import java.util.List;

public class SalesStatsDto {
    private BigDecimal totalRevenue;
    private Integer totalOrders;
    private Integer totalProducts;
    private List<CollaboratorSalesDto> collaboratorSales;
    private List<ProductSalesDto> productSales;
    private List<PaymentMethodStatsDto> paymentMethods;
    
    // Constructors
    public SalesStatsDto() {}
    
    public SalesStatsDto(BigDecimal totalRevenue, Integer totalOrders, Integer totalProducts) {
        this.totalRevenue = totalRevenue;
        this.totalOrders = totalOrders;
        this.totalProducts = totalProducts;
    }
    
    // Getters and setters
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
    
    public Integer getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Integer totalOrders) { this.totalOrders = totalOrders; }
    
    public Integer getTotalProducts() { return totalProducts; }
    public void setTotalProducts(Integer totalProducts) { this.totalProducts = totalProducts; }
    
    public List<CollaboratorSalesDto> getCollaboratorSales() { return collaboratorSales; }
    public void setCollaboratorSales(List<CollaboratorSalesDto> collaboratorSales) { 
        this.collaboratorSales = collaboratorSales; 
    }
    
    public List<ProductSalesDto> getProductSales() { return productSales; }
    public void setProductSales(List<ProductSalesDto> productSales) { this.productSales = productSales; }
    
    public List<PaymentMethodStatsDto> getPaymentMethods() { return paymentMethods; }
    public void setPaymentMethods(List<PaymentMethodStatsDto> paymentMethods) { 
        this.paymentMethods = paymentMethods; 
    }
}