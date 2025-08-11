package com.desitech.vyaparsathi.sales.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SaleItemDto {
    private Long id;
    @NotNull(message = "Item ID cannot be null")
    private Long itemId;
    private Long itemVariantId;
    @NotBlank(message = "Item name cannot be blank")
    private String itemName;
    @NotNull(message = "Quantity cannot be null")
    @Min(value = 0, message = "Quantity must be a positive number")
    private BigDecimal qty;
    @NotNull(message = "Unit price cannot be null")
    private BigDecimal unitPrice;
    private BigDecimal discount = BigDecimal.ZERO;
    private int gstRate;
    private BigDecimal taxableValue;
}