package com.aec.statssrv.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class ProductSalesDto {
    private Long productId;
    private String productName;
    private String paymentMethod;
    private BigDecimal totalSalesAmount;
    private Long totalQuantity;
    private Long orderCount;
}