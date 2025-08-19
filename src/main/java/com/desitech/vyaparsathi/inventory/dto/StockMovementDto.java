package com.desitech.vyaparsathi.inventory.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.desitech.vyaparsathi.common.util.CustomLocalDateTimeDeserializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class StockMovementDto {
    private Long id;
    private Long itemVariantId;
    private String itemName;
    private String sku;
    private String movementType; // ADD, DEDUCT, ADJUST
    private BigDecimal quantity;
    private BigDecimal costPerUnit;
    private String batch;
    private String reason;
    private String reference; // e.g., "Sale #INV-001", "Purchase Order #PO-001"
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime timestamp;
}