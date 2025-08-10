package com.desitech.desibazaar.inventory.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StockAddDto {
    private Long itemId;
    private BigDecimal quantity;
    private String batch;
}