package com.desitech.desibazaar.inventory.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class StockEntryDto {
    private Long id;
    private Long itemId;
    private BigDecimal quantity;
    private String batch;
    private LocalDateTime lastUpdated;
}