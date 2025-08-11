package com.desitech.vyaparsathi.customer.dto;

import com.desitech.vyaparsathi.customer.entity.CustomerLedgerType;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CustomerLedgerDto {
    private Long id;
    private Long customerId;
    @NotNull(message = "Amount cannot be null")
    private BigDecimal amount;
    @NotNull(message = "Type cannot be null")
    private CustomerLedgerType type;
    private String description;
    private LocalDateTime createdAt;
}