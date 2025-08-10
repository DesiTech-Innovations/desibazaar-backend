package com.desitech.desibazaar.billing.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SaleItemDto {
    private Long itemId;
    private BigDecimal qty;
    private BigDecimal unitPrice;  // Can override item's default
    private BigDecimal discount = BigDecimal.ZERO;  // Line discount
}