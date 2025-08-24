package com.desitech.vyaparsathi.payment.service;

import com.desitech.vyaparsathi.payment.dto.PaymentDto;
import com.desitech.vyaparsathi.payment.dto.PaymentReceivedRequest;
import com.desitech.vyaparsathi.payment.entity.Payment;
import com.desitech.vyaparsathi.payment.enums.PaymentMethod;
import com.desitech.vyaparsathi.payment.enums.PaymentSourceType;
import com.desitech.vyaparsathi.payment.enums.PaymentStatus;
import com.desitech.vyaparsathi.payment.repository.PaymentRepository;
import com.desitech.vyaparsathi.payment.mapper.PaymentMapper;
import com.desitech.vyaparsathi.purchaseorder.entity.PurchaseOrder;
import com.desitech.vyaparsathi.purchaseorder.repository.PurchaseOrderRepository;
import com.desitech.vyaparsathi.sales.entity.Sale;
import com.desitech.vyaparsathi.sales.repository.SaleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Override
    @Transactional
    public PaymentDto createPayment(PaymentDto dto) {
        Payment payment = paymentMapper.toEntity(dto);

        // For payments linked to a source, compute total paid and due
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalPaidBefore = BigDecimal.ZERO;
        if (payment.getSourceId() != null && payment.getSourceType() != null) {
            totalAmount = getTotalAmountForSource(payment.getSourceId(), payment.getSourceType());
            totalPaidBefore = paymentRepository.findBySourceTypeAndSourceId(
                            payment.getSourceType().name(), payment.getSourceId()
                    ).stream()
                    .map(p -> p.getAmount() != null ? p.getAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        // Add this payment
        BigDecimal paymentAmount = payment.getAmount() != null ? payment.getAmount() : BigDecimal.ZERO;
        BigDecimal totalPaidAfter = totalPaidBefore.add(paymentAmount);

        // Determine status: prefer explicit, else infer from payment method and amounts
        payment.setStatus(determinePaymentStatus(
                payment.getPaymentMethod(),
                totalPaidAfter,
                totalAmount,
                dto.getStatus()
        ));

        Payment saved = paymentRepository.save(payment);
        logger.info("Payment created: id={}, sourceType={}, sourceId={}, amount={}, status={}",
                saved.getId(), saved.getSourceType(), saved.getSourceId(), saved.getAmount(), saved.getStatus());
        return paymentMapper.toDto(saved);
    }

    /**
     * Determines the payment status using the payment method, total paid, total due, and explicit status (if present).
     */
    private PaymentStatus determinePaymentStatus(
            PaymentMethod method,
            BigDecimal totalPaid,
            BigDecimal totalAmount,
            PaymentStatus explicitStatus
    ) {
        if (explicitStatus != null) return explicitStatus;

        if (method == null) return PaymentStatus.PENDING;

        if (PaymentMethod.CASH.equals(method) || PaymentMethod.UPI.equals(method)) {
            if (totalAmount.compareTo(BigDecimal.ZERO) > 0 && totalPaid.compareTo(totalAmount) >= 0) {
                return PaymentStatus.PAID;
            } else if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
                return PaymentStatus.PARTIALLY_PAID;
            } else {
                return PaymentStatus.PENDING;
            }
        } else if (PaymentMethod.CHEQUE.equals(method) || PaymentMethod.NET_BANKING.equals(method)) {
            return PaymentStatus.PENDING;
        }
        return PaymentStatus.PENDING;
    }

    @Override
    @Cacheable("paymentsBySource")
    public List<PaymentDto> getPaymentsBySource(PaymentSourceType sourceType, Long sourceId) {
        return paymentRepository.findBySourceTypeAndSourceId(sourceType.name(), sourceId)
                .stream().map(paymentMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Cacheable("paymentsBySupplier")
    public List<PaymentDto> getPaymentsBySupplier(Long supplierId) {
        return paymentRepository.findBySupplierId(supplierId)
                .stream().map(paymentMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Cacheable("paymentsByCustomer")
    public List<PaymentDto> getPaymentsByCustomer(Long customerId) {
        return paymentRepository.findByCustomerId(customerId)
                .stream().map(paymentMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public Optional<PaymentDto> getPayment(Long id) {
        return paymentRepository.findById(id).map(paymentMapper::toDto);
    }

    /**
     * Record a due/partial payment for any source (SALE, PURCHASE_ORDER, etc.)
     */
    @Override
    @Transactional
    public PaymentDto recordDuePayment(PaymentReceivedRequest request) {
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid payment amount");
        }
        if (request.getSourceId() == null || request.getSourceType() == null) {
            throw new IllegalArgumentException("Source ID and type are required");
        }

        // Get previous payments for this source
        List<Payment> previousPayments = paymentRepository.findBySourceTypeAndSourceId(request.getSourceType().name(), request.getSourceId());
        BigDecimal totalPaidBefore = previousPayments.stream()
                .map(p -> p.getAmount() != null ? p.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAmount = getTotalAmountForSource(request.getSourceId(), request.getSourceType());
        BigDecimal totalDue = totalAmount.subtract(totalPaidBefore).max(BigDecimal.ZERO);

        if (request.getAmount().compareTo(totalDue) > 0) {
            throw new IllegalArgumentException("Payment amount exceeds due amount of " + totalDue);
        }

        // Create and populate Payment entity
        Payment payment = new Payment();
        payment.setSourceId(request.getSourceId());
        payment.setSourceType(request.getSourceType());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentDate(java.time.LocalDateTime.now());
        payment.setCustomerId(request.getCustomerId());
        payment.setSupplierId(request.getSupplierId());

        // Calculate new total paid
        BigDecimal totalPaidAfter = totalPaidBefore.add(request.getAmount());
        payment.setStatus(determinePaymentStatus(
                payment.getPaymentMethod(),
                totalPaidAfter,
                totalAmount,
                null
        ));

        Payment saved = paymentRepository.save(payment);
        logger.info("Due payment recorded: id={}, sourceType={}, sourceId={}, amount={}, status={}",
                saved.getId(), saved.getSourceType(), saved.getSourceId(), saved.getAmount(), saved.getStatus());
        return paymentMapper.toDto(saved);
    }

    @Override
    public BigDecimal calculateDueAmount(Long sourceId, PaymentSourceType sourceType, BigDecimal totalAmount) {
        List<Payment> payments = paymentRepository.findBySourceTypeAndSourceId(sourceType.name(), sourceId);
        BigDecimal totalPaid = payments.stream()
                .map(p -> p.getAmount() != null ? p.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalAmount.subtract(totalPaid).max(BigDecimal.ZERO);
    }

    public BigDecimal getTotalAmountForSource(Long sourceId, PaymentSourceType sourceType) {
        if (PaymentSourceType.SALE.equals(sourceType)) {
            Sale sale = saleRepository.findById(sourceId)
                    .orElseThrow(() -> new EntityNotFoundException("Sale not found"));
            if (sale.getTotalAmount() == null) {
                logger.warn("Total amount missing for Sale with id={}", sourceId);
                return BigDecimal.ZERO;
            }
            return sale.getTotalAmount();
        } else if (PaymentSourceType.PURCHASE_ORDER.equals(sourceType)) {
            PurchaseOrder po = purchaseOrderRepository.findById(sourceId)
                    .orElseThrow(() -> new EntityNotFoundException("Purchase Order not found"));
            if (po.getTotalAmount() == null) {
                logger.warn("Total amount missing for PurchaseOrder with id={}", sourceId);
                return BigDecimal.ZERO;
            }
            return po.getTotalAmount();
        }
        throw new IllegalArgumentException("Unsupported source type: " + sourceType);
    }
}