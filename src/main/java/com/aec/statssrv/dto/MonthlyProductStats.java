// src/main/java/com/aec/statssrv/dto/MonthlyProductStats.java
package com.aec.statssrv.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class MonthlyProductStats {
    private Long productId;
    private String productName;
    private int totalQuantity;
    private BigDecimal totalRevenue;
    private long totalDownloads;
    private String paymentMethod;
}
