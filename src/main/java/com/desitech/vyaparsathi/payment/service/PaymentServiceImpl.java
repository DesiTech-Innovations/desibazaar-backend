package com.desitech.vyaparsathi.payment.service;

import com.desitech.vyaparsathi.customer.entity.Customer;
import com.desitech.vyaparsathi.customer.entity.CustomerLedgerType;
import com.desitech.vyaparsathi.customer.service.CustomerLedgerService;
import com.desitech.vyaparsathi.customer.dto.CustomerLedgerDto;
import com.desitech.vyaparsathi.payment.entity.Payment;
import com.desitech.vyaparsathi.payment.enums.PaymentMethod;
import com.desitech.vyaparsathi.payment.enums.PaymentStatus;
import com.desitech.vyaparsathi.payment.repository.PaymentRepository;
import com.desitech.vyaparsathi.sales.entity.Sale;
import com.desitech.vyaparsathi.sales.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private CustomerLedgerService ledgerService;
    @Autowired
    private SaleRepository saleRepository; // Added to fetch Sale and Customer

    @Override
    @Transactional
    public Payment savePayment(Payment payment) {
        BigDecimal totalPaid = paymentRepository.findBySaleId(payment.getSale().getId())
                .stream()
                .map(Payment::getAmountPaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .add(payment.getAmountPaid());
        BigDecimal saleTotal = payment.getSale().getTotalAmount();
        payment.setStatus(determinePaymentStatus(totalPaid, saleTotal));
        return paymentRepository.save(payment);
    }

    @Override
    public List<Payment> getPaymentsBySaleId(Long saleId) {
        return paymentRepository.findBySaleId(saleId);
    }

    @Override
    public BigDecimal calculateDueAmount(Long saleId, BigDecimal totalAmount) {
        List<Payment> payments = paymentRepository.findBySaleId(saleId);
        BigDecimal totalPaid = payments.stream()
                .map(Payment::getAmountPaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalAmount.subtract(totalPaid).max(BigDecimal.ZERO);
    }

    @Override
    @Transactional
    public Payment recordDuePayment(Long saleId, BigDecimal amount, String method) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid payment amount");
        }

        // Fetch the sale to get the customer
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new RuntimeException("Sale not found for ID: " + saleId));
        Customer customer = sale.getCustomer();

        if (customer == null) {
            throw new IllegalArgumentException("No customer associated with this sale");
        }

        // Calculate current due amount
        BigDecimal currentDue = calculateDueAmount(saleId, sale.getTotalAmount());
        if (amount.compareTo(currentDue) > 0) {
            throw new IllegalArgumentException("Payment amount exceeds due amount of " + currentDue);
        }

        Payment payment = new Payment();
        payment.setSale(sale);
        payment.setMethod(PaymentMethod.valueOf(method.toUpperCase())); // Convert to enum
        payment.setAmountPaid(amount);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus(PaymentStatus.PAID); // Due payment is fully paid for this transaction

        // Record as DEBIT in ledger to reduce debt
        CustomerLedgerDto ledgerDto = new CustomerLedgerDto();
        ledgerDto.setAmount(amount);
        ledgerDto.setType(CustomerLedgerType.DEBIT);
        ledgerDto.setDescription("Due Payment for Sale #" + sale.getInvoiceNo() + " (" + method + ")");
        ledgerService.addEntry(customer.getId(), ledgerDto);

        // Update payment status if this clears the due
        BigDecimal totalPaid = paymentRepository.findBySaleId(saleId)
                .stream()
                .map(Payment::getAmountPaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .add(amount);
        if (totalPaid.compareTo(sale.getTotalAmount()) >= 0) {
            payment.setStatus(PaymentStatus.PAID); // Ensure status reflects full payment
        } else {
            payment.setStatus(PaymentStatus.PARTIALLY_PAID);
        }

        return paymentRepository.save(payment);
    }

    private PaymentStatus determinePaymentStatus(BigDecimal totalPaid, BigDecimal saleTotal) {
        if (totalPaid.compareTo(saleTotal) >= 0) return PaymentStatus.PAID;
        else if (totalPaid.compareTo(BigDecimal.ZERO) > 0) return PaymentStatus.PARTIALLY_PAID;
        else return PaymentStatus.PENDING;
    }
}