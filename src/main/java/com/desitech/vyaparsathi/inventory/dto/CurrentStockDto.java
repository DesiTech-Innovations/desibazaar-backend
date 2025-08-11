package com.desitech.vyaparsathi.inventory.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CurrentStockDto {
    private Long itemVariantId;
    private BigDecimal totalQuantity;
}