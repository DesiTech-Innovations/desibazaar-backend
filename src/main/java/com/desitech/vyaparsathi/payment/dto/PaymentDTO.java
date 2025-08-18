package com.desitech.vyaparsathi.payment.dto;

import com.desitech.vyaparsathi.common.util.CustomLocalDateTimeDeserializer;
import com.desitech.vyaparsathi.payment.enums.PaymentMethod;
import com.desitech.vyaparsathi.payment.enums.PaymentStatus;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentDTO {
    private Long id;
    private Long saleId;
    private PaymentMethod method;
    private BigDecimal amountPaid;
    private PaymentStatus status;
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime paymentDate;
}