package com.aec.statssrv.dto;


import java.math.BigDecimal;

public class ProductSalesDto {
    private Long productId;
    private String productName;
    private String uploaderUsername;
    private BigDecimal totalSales;
    private Integer totalQuantity;
    private Integer ordersCount;
    private BigDecimal unitPrice;
    private String country;
    
    public ProductSalesDto() {}
    
    public ProductSalesDto(Long productId, String productName, String uploaderUsername,
                          BigDecimal totalSales, Long totalQuantity, Long ordersCount,
                          BigDecimal unitPrice, String country) {
        this.productId = productId;
        this.productName = productName;
        this.uploaderUsername = uploaderUsername;
        this.totalSales = totalSales;
        this.totalQuantity = totalQuantity != null ? totalQuantity.intValue() : 0;
        this.ordersCount = ordersCount != null ? ordersCount.intValue() : 0;
        this.unitPrice = unitPrice;
        this.country = country;
    }
    
    // Getters and setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    public String getUploaderUsername() { return uploaderUsername; }
    public void setUploaderUsername(String uploaderUsername) { this.uploaderUsername = uploaderUsername; }
    
    public BigDecimal getTotalSales() { return totalSales; }
    public void setTotalSales(BigDecimal totalSales) { this.totalSales = totalSales; }
    
    public Integer getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(Integer totalQuantity) { this.totalQuantity = totalQuantity; }
    
    public Integer getOrdersCount() { return ordersCount; }
    public void setOrdersCount(Integer ordersCount) { this.ordersCount = ordersCount; }
    
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
}