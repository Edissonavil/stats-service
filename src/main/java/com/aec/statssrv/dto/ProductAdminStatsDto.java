// src/main/java/com/aec/statssrv/dto/ProductAdminStatsDto.java
package com.aec.statssrv.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
public class ProductAdminStatsDto {
    private Long productId;
    private String productName;
    private long totalSold;            // unidades vendidas
    private BigDecimal totalRevenue;   // importe total vendido
    private long totalDownloads;       // veces descargado
    private Map<String, Long> byPaymentMethod; // { "PAYPAL": 10, "MANUAL_TRANSFER": 3 }
}
