package com.desitech.vyaparsathi.sales.service;

import com.desitech.vyaparsathi.customer.dto.CustomerLedgerDto;
import com.desitech.vyaparsathi.customer.entity.CustomerLedgerType;
import com.desitech.vyaparsathi.customer.service.CustomerLedgerService;
import com.desitech.vyaparsathi.payment.dto.PaymentDTO;
import com.desitech.vyaparsathi.payment.entity.Payment;
import com.desitech.vyaparsathi.payment.enums.PaymentStatus;
import com.desitech.vyaparsathi.payment.service.PaymentService;
import com.desitech.vyaparsathi.sales.GSTType;
import com.desitech.vyaparsathi.sales.dto.SaleDto;
import com.desitech.vyaparsathi.sales.dto.SaleDueDto;
import com.desitech.vyaparsathi.sales.dto.SaleItemDto;
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
import com.desitech.vyaparsathi.catalog.entity.ItemVariant;
import com.desitech.vyaparsathi.catalog.repository.ItemVariantRepository;
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
import java.util.stream.Collectors;

@Service
public class SaleService {

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
    public byte[] createSale(SaleDto dto) {
        Shop shop = shopRepository.findById(1L).orElseThrow(() -> new RuntimeException("Shop not found"));
        Optional<Customer> customerOpt = Optional.ofNullable(dto.getCustomerId()).flatMap(customerRepository::findById);
        Customer customer = customerOpt.orElse(null);

        List<SaleItem> saleItems = new ArrayList<>();
        BigDecimal totalTaxableValue = BigDecimal.ZERO;
        BigDecimal totalGSTAmount = BigDecimal.ZERO;
        BigDecimal totalCOGS = BigDecimal.ZERO; // Track total COGS for this sale

        for (SaleItemDto itemDto : dto.getItems()) {
            if (!stockService.isStockAvailable(itemDto.getItemVariantId(), itemDto.getQty())) {
                throw new InsufficientStockException("Insufficient stock for item: " + itemDto.getItemName());
            }

            ItemVariant itemVariant = itemVariantRepository.findById(itemDto.getItemVariantId())
                    .orElseThrow(() -> new RuntimeException("Item Variant not found for ID: " + itemDto.getItemVariantId()));

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
            stockService.deductStock(itemDto.getItemVariantId(), itemDto.getQty());
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
            for (PaymentDTO paymentDTO : dto.getPaymentDetails()) {
                if (paymentDTO.getAmountPaid() == null) {
                    throw new IllegalArgumentException("Payment amount cannot be null");
                }
                Payment payment = new Payment();
                payment.setSale(sale);
                payment.setMethod(paymentDTO.getMethod());
                payment.setAmountPaid(paymentDTO.getAmountPaid());
                payment.setPaymentDate(LocalDateTime.now());
                payment = paymentService.savePayment(payment); // Let PaymentService handle status
                totalPaid = totalPaid.add(paymentDTO.getAmountPaid());

                if (customer != null) {
                    CustomerLedgerDto ledgerDto = new CustomerLedgerDto();
                    ledgerDto.setAmount(paymentDTO.getAmountPaid());
                    ledgerDto.setType(CustomerLedgerType.DEBIT); // Payment reduces debt
                    ledgerDto.setDescription("Payment for Sale #" + invoiceNo + " (" + paymentDTO.getMethod() + ")");
                    ledgerService.addEntry(customer.getId(), ledgerDto);
                }
            }

            BigDecimal unpaidAmount = finalTotalAmount.subtract(totalPaid);
            if (unpaidAmount.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Total paid cannot exceed sale amount");
            }
        }

        changeLogService.append("SALE", sale.getId(), "CREATE", sale, "LOCAL_DEVICE");

        return invoiceService.generatePdf(sale);
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
        return mapper.toDtoList(sales);
    }

    private PaymentStatus determinePaymentStatus(BigDecimal totalAmount, List<PaymentDTO> payments) {
        BigDecimal totalPaid = payments.stream()
                .map(PaymentDTO::getAmountPaid)
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
                    List<Payment> payments = sale.getPayments(); // Eager-loaded
                    BigDecimal paidAmount = payments.stream()
                            .map(Payment::getAmountPaid)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal dueAmount = paymentService.calculateDueAmount(sale.getId(), totalAmount);
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
                    dto.setDueAmount(paymentService.calculateDueAmount(sale.getId(), sale.getTotalAmount()));
                    return dto;
                })
                .orElseThrow(() -> new RuntimeException("Sale not found with ID: " + saleId));
    }

    public Page<SaleDueDto> getDuesByCustomerId(Long customerId, Pageable pageable) {
        Page<Sale> sales = saleRepository.findByCustomerId(customerId, pageable);
        List<SaleDueDto> filtered = sales.stream()
                .map(sale -> {
                    BigDecimal totalAmount = sale.getTotalAmount();
                    List<Payment> payments = sale.getPayments(); // Should be eager-loaded now
                    BigDecimal paidAmount = payments.stream()
                            .map(Payment::getAmountPaid)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal dueAmount = paymentService.calculateDueAmount(sale.getId(), totalAmount);
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