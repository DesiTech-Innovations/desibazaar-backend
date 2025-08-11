package com.desitech.vyaparsathi.sales.service;

import com.desitech.vyaparsathi.sales.dto.SaleDto;
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
    private ItemVariantRepository itemVariantRepository; // Inject the correct repository

    @Transactional
    public byte[] createSale(SaleDto dto) {
        Shop shop = shopRepository.findById(1L).orElseThrow(() -> new RuntimeException("Shop not found"));
        Optional<Customer> customerOpt = Optional.ofNullable(dto.getCustomerId()).flatMap(customerRepository::findById);
        Customer customer = customerOpt.orElse(null);

        List<SaleItem> saleItems = new ArrayList<>();
        BigDecimal totalTaxableValue = BigDecimal.ZERO;
        BigDecimal totalGSTAmount = BigDecimal.ZERO;

        for (SaleItemDto itemDto : dto.getItems()) {
            // First, check if the stock is available.
            if (!stockService.isStockAvailable(itemDto.getItemVariantId(), itemDto.getQty())) {
                throw new InsufficientStockException("Insufficient stock for item: " + itemDto.getItemName());
            }

            // Fetch the ItemVariant to get accurate, up-to-date data for the sale.
            ItemVariant itemVariant = itemVariantRepository.findById(itemDto.getItemVariantId())
                    .orElseThrow(() -> new RuntimeException("Item Variant not found for ID: " + itemDto.getItemVariantId()));

            // Now that we know stock is available, proceed with calculations and deduction.
            BigDecimal taxableValue = itemDto.getQty().multiply(itemDto.getUnitPrice()).subtract(itemDto.getDiscount());
            int gstRate = itemVariant.getGstRate(); // Use the GST rate from the fetched ItemVariant
            BigDecimal gstAmount = taxableValue.multiply(BigDecimal.valueOf(gstRate)).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            SaleItem saleItem = new SaleItem();
            saleItem.setItemVariant(itemVariant);
            saleItem.setQty(itemDto.getQty());
            saleItem.setUnitPrice(itemDto.getUnitPrice());
            saleItem.setTaxableValue(taxableValue);
            saleItem.setGstRate(gstRate);

            // GST logic: CGST+SGST if same state, else IGST
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

            saleItems.add(saleItem);
            totalTaxableValue = totalTaxableValue.add(taxableValue);
            totalGSTAmount = totalGSTAmount.add(gstAmount);

            // Deduct stock after the check
            stockService.deductStock(itemDto.getItemVariantId(), itemDto.getQty());
        }

        BigDecimal totalAmountBeforeRoundOff = totalTaxableValue.add(totalGSTAmount);
        BigDecimal roundOff = totalAmountBeforeRoundOff.subtract(totalAmountBeforeRoundOff.setScale(0, RoundingMode.HALF_UP));
        BigDecimal finalTotalAmount = totalAmountBeforeRoundOff.setScale(0, RoundingMode.HALF_UP);

        // Generate invoice no: SHOPCODE-YYYYMM-SEQ (use count as seq since SQLite lacks sequences)
        // NOTE: This is a simplistic approach and not robust for concurrent environments.
        // A dedicated sequence generator table or a database sequence is recommended for production.
        String seq = String.format("%03d", saleRepository.count() + 1);
        String invoiceNo = shop.getCode() + "-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM")) + "-" + seq;

        Sale sale = new Sale();
        sale.setInvoiceNo(invoiceNo);
        sale.setShop(shop);
        sale.setCustomer(customer);
        sale.setTotalAmount(finalTotalAmount);
        sale.setRoundOff(roundOff.negate());
        sale.setPaymentMethod(dto.getPaymentMethod());
        sale.setSyncedFlag(false);
        sale.setSaleItems(saleItems);
        saleItems.forEach(si -> si.setSale(sale));

        saleRepository.save(sale);
        changeLogService.append("SALE", sale.getId(), "CREATE", sale, "LOCAL_DEVICE");

        // Generate PDF
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
}