package com.desitech.vyaparsathi.payment.service;

import com.desitech.vyaparsathi.payment.dto.PaymentDto;
import com.desitech.vyaparsathi.payment.dto.PaymentReceivedRequest;
import com.desitech.vyaparsathi.payment.enums.PaymentSourceType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PaymentService {
    PaymentDto createPayment(PaymentDto dto);
    List<PaymentDto> getPaymentsBySource(PaymentSourceType sourceType, Long sourceId);
    List<PaymentDto> getPaymentsBySupplier(Long supplierId);
    List<PaymentDto> getPaymentsByCustomer(Long customerId);
    Optional<PaymentDto> getPayment(Long id);

    BigDecimal calculateDueAmount(Long sourceId, PaymentSourceType sourceType, BigDecimal totalAmount);
    PaymentDto recordDuePayment(PaymentReceivedRequest paymentReceivedRequest);
}
