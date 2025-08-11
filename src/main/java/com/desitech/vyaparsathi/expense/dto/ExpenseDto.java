package com.desitech.vyaparsathi.expense.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ExpenseDto {
    private Long id;
    @NotNull(message = "Shop ID cannot be null")
    private Long shopId;
    @NotBlank(message = "Expense type cannot be blank")
    private String type;
    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    private LocalDateTime date = LocalDateTime.now();
    private String notes;
}