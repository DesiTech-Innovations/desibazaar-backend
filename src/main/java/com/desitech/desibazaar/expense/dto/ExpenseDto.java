package com.desitech.desibazaar.expense.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ExpenseDto {
    private Long id;
    private Long shopId;
    private String type;
    private BigDecimal amount;
    private LocalDateTime date;
    private String notes;
}