package com.desitech.vyaparsathi.sales.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SaleDto {
    private Long id;
    private Long customerId;
    @NotNull(message = "Items list cannot be null")
    @NotEmpty(message = "Items list cannot be empty")
    @Valid
    private List<SaleItemDto> items;
    private String paymentMethod;
    private BigDecimal totalAmount;
    private BigDecimal roundOff;
    private LocalDateTime date;
}