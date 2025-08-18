package com.desitech.vyaparsathi.payment.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentReceivedRequest {
    private Long saleId;
    private BigDecimal amount;
    private String method;
}
