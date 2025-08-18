package com.desitech.vyaparsathi.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {
    private Long id;
    private BigDecimal amountPaid;
    private String method;
    private String status;
    private LocalDateTime paymentDate;
}
