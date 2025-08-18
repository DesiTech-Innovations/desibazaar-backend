package com.desitech.vyaparsathi.payment.service;

import com.desitech.vyaparsathi.payment.entity.Payment;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentService {
    Payment savePayment(Payment payment);
    List<Payment> getPaymentsBySaleId(Long saleId);
    BigDecimal calculateDueAmount(Long saleId, BigDecimal totalAmount);

    Payment recordDuePayment(Long saleId, BigDecimal amount, String method);
}
