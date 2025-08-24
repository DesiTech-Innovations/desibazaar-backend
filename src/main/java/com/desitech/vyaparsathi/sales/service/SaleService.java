package com.desitech.vyaparsathi.sales.service;

import com.desitech.vyaparsathi.common.exception.EntityNotFoundAppException;
import com.desitech.vyaparsathi.common.exception.ValidationAppException;
import com.desitech.vyaparsathi.payment.enums.PaymentSourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desitech.vyaparsathi.customer.dto.CustomerLedgerDto;
import com.desitech.vyaparsathi.customer.entity.CustomerLedgerType;
import com.desitech.vyaparsathi.customer.service.CustomerLedgerService;
import com.desitech.vyaparsathi.payment.dto.PaymentDto;
import com.desitech.vyaparsathi.payment.entity.Payment;
import com.desitech.vyaparsathi.payment.enums.PaymentStatus;
import com.desitech.vyaparsathi.payment.service.PaymentService;
import com.desitech.vyaparsathi.sales.GSTType;
import com.desitech.vyaparsathi.sales.dto.SaleDto;
import com.desitech.vyaparsathi.sales.dto.SaleDueDto;
import com.desitech.vyaparsathi.sales.dto.SaleItemDto;
import com.desitech.vyaparsathi.sales.dto.SaleReturnDto;
import com.desitech.vyaparsathi.sales.dto.SalesProfitDto;
import com.desitech.vyaparsathi.sales.entity.Sale;
import com.desitech.vyaparsathi.sales.entity.SaleItem;
import com.desitech.vyaparsathi.sales.mapper.SaleMapper;
import com.desitech.vyaparsathi.sales.repository.SaleRepository;
import com.desitech.vyaparsathi.changelog.service.ChangeLogService;
import com.desitech.vyaparsathi.customer.entity.Customer;
import com.desitech.vyaparsathi.customer.repository.CustomerRepository;
import com.desitech.vyaparsathi.inventory.service.StockService;
import com.desitech.vyaparsathi.shop.entity.Shop;
import com.desitech.vyaparsathi.shop.repository.ShopRepository;
import com.desitech.vyaparsathi.common.exception.InsufficientStockException;
import com.desitech.vyaparsathi.inventory.entity.ItemVariant;
import com.desitech.vyaparsathi.inventory.repository.ItemVariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SaleService {

    private static final Logger logger = LoggerFactory.getLogger(SaleService.class);

    @Autowired
    private SaleRepository saleRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private StockService stockService;
    @Autowired
    private ChangeLogService changeLogService;
    @Autowired
    private InvoiceService invoiceService;
    @Autowired
    private SaleMapper mapper;
    @Autowired
    private ItemVariantRepository itemVariantRepository;

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private CustomerLedgerService ledgerService;

    @Transactional
    public byte[] createSale(SaleDto dto) throws com.lowagie.text.BadElementException {
    Shop shop = shopRepository.findById(1L).orElseThrow(() -> new EntityNotFoundAppException("Shop", 1L));
    Optional<Customer> customerOpt = Optional.ofNullable(dto.getCustomerId()).flatMap(customerRepository::findById);
    Customer customer = customerOpt.orElse(null);

        List<SaleItem> saleItems = new ArrayList<>();
        BigDecimal totalTaxableValue = BigDecimal.ZERO;
        BigDecimal totalGSTAmount = BigDecimal.ZERO;
        BigDecimal totalCOGS = BigDecimal.ZERO; // Track total COGS for this sale

        for (SaleItemDto itemDto : dto.getItems()) {
            if (!stockService.isStockAvailable(itemDto.getItemVariantId(), itemDto.getQty())) {
                logger.warn("Insufficient stock for item: {}", itemDto.getItemName());
                throw new InsufficientStockException("Insufficient stock for item: " + itemDto.getItemName());
            }

            ItemVariant itemVariant = itemVariantRepository.findById(itemDto.getItemVariantId())
                    .orElseThrow(() -> new EntityNotFoundAppException("Item Variant", itemDto.getItemVariantId()));

            // Calculate COGS for this item using FIFO
            BigDecimal itemCOGS = stockService.calculateCOGSFifo(itemDto.getItemVariantId(), itemDto.getQty());
            BigDecimal costPerUnit = itemCOGS.divide(itemDto.getQty(), 4, RoundingMode.HALF_UP);
            totalCOGS = totalCOGS.add(itemCOGS);

            SaleItem saleItem = new SaleItem();
            saleItem.setItemVariant(itemVariant);
            saleItem.setQty(itemDto.getQty());
            saleItem.setUnitPrice(itemDto.getUnitPrice());
            saleItem.setCostPerUnit(costPerUnit); // Set the cost per unit for this sale item

            if (Boolean.TRUE.equals(dto.getIsGstRequired()) && itemVariant.getGstRate() != null) {
                GSTType gstType = GSTType.fromRate(itemVariant.getGstRate());

                BigDecimal taxableValue = itemDto.getQty()
                        .multiply(itemDto.getUnitPrice())
                        .subtract(itemDto.getDiscount() != null ? itemDto.getDiscount() : BigDecimal.ZERO);

                BigDecimal gstAmount = taxableValue
                        .multiply(BigDecimal.valueOf(gstType.getRate()))
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                saleItem.setTaxableValue(taxableValue);
                saleItem.setGstType(gstType);

                boolean sameState = customer != null && shop.getState().equals(customer.getState());
                if (sameState) {
                    BigDecimal half = gstAmount.divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);
                    saleItem.setCgstAmt(half);
                    saleItem.setSgstAmt(half);
                    saleItem.setIgstAmt(BigDecimal.ZERO);
                } else {
                    saleItem.setIgstAmt(gstAmount);
                    saleItem.setCgstAmt(BigDecimal.ZERO);
                    saleItem.setSgstAmt(BigDecimal.ZERO);
                }

                totalTaxableValue = totalTaxableValue.add(taxableValue);
                totalGSTAmount = totalGSTAmount.add(gstAmount);
            } else {
                saleItem.setTaxableValue(BigDecimal.ZERO);
                saleItem.setGstType(GSTType.GST_0);
                saleItem.setCgstAmt(BigDecimal.ZERO);
                saleItem.setSgstAmt(BigDecimal.ZERO);
                saleItem.setIgstAmt(BigDecimal.ZERO);
            }

            saleItems.add(saleItem);
        }

        // Deduct stock for all items after successful validation
        for (SaleItemDto itemDto : dto.getItems()) {
            stockService.deductStock(itemDto.getItemVariantId(), itemDto.getQty(), 
                                   "Sale Transaction", "Sale - Processing");
        }

        BigDecimal totalAmountBeforeRoundOff;
        if (Boolean.TRUE.equals(dto.getIsGstRequired())) {
            totalAmountBeforeRoundOff = totalTaxableValue.add(totalGSTAmount);
        } else {
            totalAmountBeforeRoundOff = dto.getItems().stream()
                    .map(item -> item.getQty().multiply(item.getUnitPrice()).subtract(item.getDiscount() != null ? item.getDiscount() : BigDecimal.ZERO))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        BigDecimal roundOff = totalAmountBeforeRoundOff.subtract(totalAmountBeforeRoundOff.setScale(0, RoundingMode.HALF_UP));
        BigDecimal finalTotalAmount = totalAmountBeforeRoundOff.setScale(0, RoundingMode.HALF_UP);

        String seq = String.format("%03d", saleRepository.count() + 1);
        String invoiceNo = shop.getCode() + "-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM")) + "-" + seq;

        Sale sale = new Sale();
        sale.setInvoiceNo(invoiceNo);
        sale.setShop(shop);
        sale.setCustomer(customer);
        sale.setTotalAmount(finalTotalAmount);
        sale.setCogs(totalCOGS); // Set the total COGS for this sale
        sale.setRoundOff(roundOff.negate());
        sale.setSyncedFlag(false);
        sale.setSaleItems(saleItems);
        saleItems.forEach(si -> si.setSale(sale));

        saleRepository.save(sale); // Save sale first to ensure ID is set

        // Record the total sale amount as credit (initial debt)
        if (customer != null) {
            CustomerLedgerDto saleLedgerDto = new CustomerLedgerDto();
            saleLedgerDto.setAmount(finalTotalAmount);
            saleLedgerDto.setType(CustomerLedgerType.CREDIT); // Sale increases debt
            saleLedgerDto.setDescription("Sale #" + invoiceNo);
            ledgerService.addEntry(customer.getId(), saleLedgerDto);
        }

        if (dto.getPaymentDetails() != null) {
            BigDecimal totalPaid = BigDecimal.ZERO;
            for (PaymentDto paymentDTO : dto.getPaymentDetails()) {
                if (paymentDTO.getAmount() == null) {
                    logger.error("Payment amount cannot be null for sale DTO: {}", dto);
                    throw new ValidationAppException("Payment amount cannot be null");
                }
                paymentDTO.setSourceId(sale.getId());
                paymentDTO.setSourceType(PaymentSourceType.SALE);
                paymentDTO.setCustomerId(customer != null ? customer.getId() : null);
                paymentDTO.setPaymentDate(LocalDateTime.now());
                //paymentDTO.setStatus(PaymentStatus.PAID);
                PaymentDto savedPayment = paymentService.createPayment(paymentDTO);
                totalPaid = totalPaid.add(paymentDTO.getAmount());

                if (customer != null) {
                    CustomerLedgerDto ledgerDto = new CustomerLedgerDto();
                    ledgerDto.setAmount(paymentDTO.getAmount());
                    ledgerDto.setType(CustomerLedgerType.DEBIT); // Payment reduces debt
                    ledgerDto.setDescription("Payment for Sale #" + invoiceNo + " (" + paymentDTO.getPaymentMethod() + ")");
                    ledgerService.addEntry(customer.getId(), ledgerDto);
                }
            }

            BigDecimal unpaidAmount = finalTotalAmount.subtract(totalPaid);
            if (unpaidAmount.compareTo(BigDecimal.ZERO) < 0) {
                logger.error("Total paid ({}) exceeds sale amount ({})", totalPaid, finalTotalAmount);
                throw new ValidationAppException("Total paid cannot exceed sale amount");
            }
        }

    changeLogService.append("SALE", sale.getId(), com.desitech.vyaparsathi.changelog.model.ChangeLogOperation.CREATE, sale, "LOCAL_DEVICE");

        return invoiceService.generatePdf(sale);
    }

    /**
     * Process sale return
     */
    @Transactional
    public void processSaleReturn(SaleReturnDto returnDto) {
    Sale sale = saleRepository.findById(returnDto.getSaleId())
        .orElseThrow(() -> new EntityNotFoundAppException("Sale", returnDto.getSaleId()));

        BigDecimal totalReturnAmount = BigDecimal.ZERO;

        for (SaleReturnDto.SaleReturnItemDto returnItem : returnDto.getReturnItems()) {
        SaleItem saleItem = sale.getSaleItems().stream()
            .filter(si -> si.getId().equals(returnItem.getSaleItemId()))
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundAppException("Sale item", returnItem.getSaleItemId()));

            if (returnItem.getReturnQuantity().compareTo(saleItem.getQty()) > 0) {
                logger.error("Return quantity ({}) exceeds original quantity ({}) for sale item {}", returnItem.getReturnQuantity(), saleItem.getQty(), saleItem.getId());
                throw new ValidationAppException("Return quantity cannot exceed original quantity");
            }

            // Calculate return amount (proportional to original)
            BigDecimal returnRatio = returnItem.getReturnQuantity().divide(saleItem.getQty(), 4, RoundingMode.HALF_UP);
            BigDecimal itemReturnAmount = saleItem.getUnitPrice().multiply(returnItem.getReturnQuantity());
            totalReturnAmount = totalReturnAmount.add(itemReturnAmount);

            // Return stock to inventory
            stockService.addStock(saleItem.getItemVariant().getId(), 
                                returnItem.getReturnQuantity(), 
                                saleItem.getCostPerUnit(), 
                                "Return from Sale #" + sale.getInvoiceNo());

            // Update sale item quantity
            saleItem.setQty(saleItem.getQty().subtract(returnItem.getReturnQuantity()));
        }

        // Update sale total amount
    sale.setTotalAmount(sale.getTotalAmount().subtract(totalReturnAmount));
    sale.setCogs(sale.getCogs().subtract(calculateReturnCOGS(returnDto)));
    logger.info("Processed sale return for sale ID {}: return amount {}", sale.getId(), totalReturnAmount);
        
        saleRepository.save(sale);

        // Handle payment refund if requested
        if (returnDto.isRefundPayment() && sale.getCustomer() != null) {
            CustomerLedgerDto ledgerDto = new CustomerLedgerDto();
            ledgerDto.setAmount(totalReturnAmount);
            ledgerDto.setType(CustomerLedgerType.DEBIT); // Refund reduces customer debt
            ledgerDto.setDescription("Return for Sale #" + sale.getInvoiceNo() + 
                                   (returnDto.getReason() != null ? " - " + returnDto.getReason() : ""));
            ledgerService.addEntry(sale.getCustomer().getId(), ledgerDto);
        }

        // Log the return
    changeLogService.append("SALE_RETURN", sale.getId(), com.desitech.vyaparsathi.changelog.model.ChangeLogOperation.RETURN, returnDto, "LOCAL_DEVICE");
    }

    /**
     * Cancel entire sale
     */
    @Transactional
    public void cancelSale(Long saleId, String reason) {
    Sale sale = saleRepository.findById(saleId)
        .orElseThrow(() -> new EntityNotFoundAppException("Sale", saleId));

        // Restore stock for all items
        for (SaleItem saleItem : sale.getSaleItems()) {
            stockService.addStock(saleItem.getItemVariant().getId(), 
                                saleItem.getQty(), 
                                saleItem.getCostPerUnit(), 
                                "Cancelled Sale #" + sale.getInvoiceNo());
        }

        // Reverse customer ledger entries if customer exists
        if (sale.getCustomer() != null) {
            // Reverse the original sale entry
            CustomerLedgerDto reverseDto = new CustomerLedgerDto();
            reverseDto.setAmount(sale.getTotalAmount());
            reverseDto.setType(CustomerLedgerType.DEBIT); // Reverse the credit
            reverseDto.setDescription("Cancelled Sale #" + sale.getInvoiceNo() + 
                                    (reason != null ? " - " + reason : ""));
            ledgerService.addEntry(sale.getCustomer().getId(), reverseDto);

            // Reverse any payment entries
            List<PaymentDto> payments = paymentService.getPaymentsBySource(PaymentSourceType.SALE, sale.getId());
            for (PaymentDto payment : payments) {
                CustomerLedgerDto paymentReverseDto = new CustomerLedgerDto();
                paymentReverseDto.setAmount(payment.getAmount());
                paymentReverseDto.setType(CustomerLedgerType.CREDIT); // Reverse the debit
                paymentReverseDto.setDescription("Cancelled Payment for Sale #" + sale.getInvoiceNo() + 
                                               " (" + payment.getPaymentMethod() + ")");
                ledgerService.addEntry(sale.getCustomer().getId(), paymentReverseDto);
            }
        }

        // Mark sale as cancelled (you might want to add a status field to Sale entity)
    sale.setTotalAmount(BigDecimal.ZERO);
    sale.setCogs(BigDecimal.ZERO);
    saleRepository.save(sale);
    logger.info("Cancelled sale with ID {}", saleId);

        // Log the cancellation
    changeLogService.append("SALE_CANCEL", sale.getId(), com.desitech.vyaparsathi.changelog.model.ChangeLogOperation.CANCEL, 
                   java.util.Map.of("reason", reason), "LOCAL_DEVICE");
    }

    /**
     * Get sales profit report
     */
    public List<SalesProfitDto> getSalesProfitReport(LocalDateTime startDate, LocalDateTime endDate) {
        List<Sale> sales = saleRepository.findByDateBetween(startDate, endDate);
        
        return sales.stream().map(sale -> {
            BigDecimal grossProfit = sale.getTotalAmount().subtract(sale.getCogs());
            BigDecimal marginPercent = sale.getTotalAmount().compareTo(BigDecimal.ZERO) > 0 
                ? grossProfit.divide(sale.getTotalAmount(), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

            return new SalesProfitDto(
                sale.getId(),
                sale.getInvoiceNo(),
                sale.getDate(),
                sale.getTotalAmount(),
                sale.getCogs(),
                grossProfit,
                marginPercent,
                sale.getCustomer() != null ? sale.getCustomer().getName() : "Walk-in Customer"
            );
        }).collect(Collectors.toList());
    }

    private BigDecimal calculateReturnCOGS(SaleReturnDto returnDto) {
        BigDecimal totalReturnCOGS = BigDecimal.ZERO;
        
    Sale sale = saleRepository.findById(returnDto.getSaleId())
        .orElseThrow(() -> new EntityNotFoundAppException("Sale", returnDto.getSaleId()));

        for (SaleReturnDto.SaleReturnItemDto returnItem : returnDto.getReturnItems()) {
        SaleItem saleItem = sale.getSaleItems().stream()
            .filter(si -> si.getId().equals(returnItem.getSaleItemId()))
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundAppException("Sale item", returnItem.getSaleItemId()));

            BigDecimal returnCOGS = saleItem.getCostPerUnit().multiply(returnItem.getReturnQuantity());
            totalReturnCOGS = totalReturnCOGS.add(returnCOGS);
        }

        return totalReturnCOGS;
    }

    public Optional<SaleDto> getSaleById(Long id) {
    return saleRepository.findById(id).map(mapper::toDto);
    }

    public List<SaleDto> listSales(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null) {
            startDate = LocalDateTime.of(1970, 1, 1, 0, 0);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }
    List<Sale> sales = saleRepository.findByDateBetween(startDate, endDate);
    logger.info("Fetched {} sales between {} and {}", sales.size(), startDate, endDate);
    return mapper.toDtoList(sales);
    }

    private PaymentStatus determinePaymentStatus(BigDecimal totalAmount, List<PaymentDto> payments) {
    BigDecimal totalPaid = payments.stream()
        .map(PaymentDto::getAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (totalPaid.compareTo(totalAmount) >= 0) return PaymentStatus.PAID;
        if (totalPaid.compareTo(BigDecimal.ZERO) > 0) return PaymentStatus.PARTIALLY_PAID;
        return PaymentStatus.PENDING;
    }

    public List<SaleDueDto> getSalesWithDue() {
        List<Sale> sales = saleRepository.findAll(); // Custom query with join fetch
    return sales.stream()
        .map(sale -> {
            BigDecimal totalAmount = sale.getTotalAmount();
            List<PaymentDto> payments = paymentService.getPaymentsBySource(PaymentSourceType.SALE, sale.getId());
            BigDecimal paidAmount = payments.stream()
                .map(p -> p.getAmount() != null ? p.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal dueAmount = paymentService.calculateDueAmount(sale.getId(), PaymentSourceType.SALE, totalAmount);
            return new SaleDueDto(
                sale.getId(),
                sale.getInvoiceNo(),
                dueAmount,
                sale.getCustomer().getId(),
                sale.getDate(),
                sale.getTotalAmount(),
                paidAmount,
                sale.getCustomer().getName(),
                sale.getCustomer().getAddressLine1(),
                sale.getCustomer().getCity(),
                sale.getCustomer().getState(),
                sale.getCustomer().getPostalCode()
            );
        })
        .collect(Collectors.toList());
    }

    public SaleDueDto getSaleDueBySaleId(Long saleId) {
        return saleRepository.findById(saleId)
                .map(sale -> {
                    SaleDueDto dto = new SaleDueDto();
                    dto.setSaleId(sale.getId());
                    dto.setInvoiceNo(sale.getInvoiceNo());
                    BigDecimal dueAmount = paymentService.calculateDueAmount(sale.getId(), PaymentSourceType.SALE, sale.getTotalAmount());
                    dto.setDueAmount(dueAmount);
                    return dto;
                })
                .orElseThrow(() -> new EntityNotFoundAppException("Sale", saleId));
    }

    public Page<SaleDueDto> getDuesByCustomerId(Long customerId, Pageable pageable) {
        Page<Sale> sales = saleRepository.findByCustomerId(customerId, pageable);
    List<SaleDueDto> filtered = sales.stream()
        .map(sale -> {
            BigDecimal totalAmount = sale.getTotalAmount();
            List<PaymentDto> payments = paymentService.getPaymentsBySource(PaymentSourceType.SALE, sale.getId());
            BigDecimal paidAmount = payments.stream()
                .map(p -> p.getAmount() != null ? p.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal dueAmount = paymentService.calculateDueAmount(sale.getId(), PaymentSourceType.SALE, totalAmount);
            return new SaleDueDto(
                sale.getId(),
                sale.getInvoiceNo(),
                dueAmount,
                sale.getCustomer().getId(),
                sale.getDate(),
                totalAmount,
                paidAmount,
                sale.getCustomer().getName(),
                sale.getCustomer().getAddressLine1(),
                sale.getCustomer().getCity(),
                sale.getCustomer().getState(),
                sale.getCustomer().getPostalCode()
            );
        })
        .filter(dto -> dto.getDueAmount().compareTo(BigDecimal.ZERO) > 0) // Only include dues > 0
        .toList();

        return new PageImpl<>(filtered, pageable, sales.getTotalElements());
    }
}