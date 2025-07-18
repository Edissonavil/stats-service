// src/main/java/com/aec/statssrv/dto/OrderDto.java
package com.aec.statssrv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private Long id;
    private String orderId;
    private List<ItemDto> items;
    private double total;
    private Instant creadoEn;
    private String status;
    // <- este es el que necesita el getter getPaymentMethod()
    private String paymentMethod;  
    private String paymentStatus;
    private String downloadUrl;
    private String customerUsername;
    private String customerEmail;
    private String customerFullName;
    private String receiptFilename;
    private String adminApprovalUser;
    private Instant approvalDate;
    private String adminComment;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemDto {
        private Long id;
        private Long productId;
        private int cantidad;
        private java.math.BigDecimal precioUnitario;
        private java.math.BigDecimal subtotal;
    }
}
