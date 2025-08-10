package com.desitech.desibazaar.inventory.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CurrentStockDto {
    private Long itemId;
    private BigDecimal totalQuantity;
}