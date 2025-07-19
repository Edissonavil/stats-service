package com.aec.statssrv.dto;

import java.math.BigDecimal;

public class PaymentMethodStatsDto {
    private String paymentMethod;
    private BigDecimal totalAmount;
    private Integer transactionCount;
    private Double percentage;
    
    public PaymentMethodStatsDto() {}
    
    public PaymentMethodStatsDto(String paymentMethod, BigDecimal totalAmount, Long transactionCount) {
        this.paymentMethod = paymentMethod;
        this.totalAmount = totalAmount;
        this.transactionCount = transactionCount != null ? transactionCount.intValue() : 0;
    }
    
    // Getters and setters
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public Integer getTransactionCount() { return transactionCount; }
    public void setTransactionCount(Integer transactionCount) { this.transactionCount = transactionCount; }
    
    public Double getPercentage() { return percentage; }
    public void setPercentage(Double percentage) { this.percentage = percentage; }
}
