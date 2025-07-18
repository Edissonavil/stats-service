package com.aec.statssrv.dto;

import java.math.BigDecimal;

public record OrderItemDto(
    Long        productId,
    int         cantidad,
    BigDecimal  subtotal
) {}
