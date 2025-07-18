// src/main/java/com/aec/statssrv/model/MonthlyProductStats.java
package com.aec.statssrv.model;

import java.math.BigDecimal;
import java.util.Map;

public class MonthlyProductStats {
    private Long productId;
    private String productName;
    private long totalQuantitySold;
    private BigDecimal totalRevenueUsd;
    private Map<String, Long> salesByPaymentType;

    // getters / setters
    public Long getProductId()                { return productId; }
    public String getProductName()            { return productName; }
    public long getTotalQuantitySold()        { return totalQuantitySold; }
    public BigDecimal getTotalRevenueUsd()    { return totalRevenueUsd; }
    public Map<String, Long> getSalesByPaymentType() { return salesByPaymentType; }
    public void setProductId(Long id)         { this.productId = id; }
    public void setProductName(String n)      { this.productName = n; }
    public void setTotalQuantitySold(long q)  { this.totalQuantitySold = q; }
    public void setTotalRevenueUsd(BigDecimal r) { this.totalRevenueUsd = r; }
    public void setSalesByPaymentType(Map<String, Long> m) { this.salesByPaymentType = m; }
}
