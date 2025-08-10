package com.desitech.desibazaar.billing.service;

import com.desitech.desibazaar.billing.dto.SaleDto;
import com.desitech.desibazaar.billing.dto.SaleItemDto;
import com.desitech.desibazaar.billing.entity.Sale;
import com.desitech.desibazaar.billing.entity.SaleItem;
import com.desitech.desibazaar.billing.repository.SaleRepository;
import com.desitech.desibazaar.catalog.entity.Item;
import com.desitech.desibazaar.catalog.repository.ItemRepository;
import com.desitech.desibazaar.changelog.service.ChangeLogService;
import com.desitech.desibazaar.customer.entity.Customer;
import com.desitech.desibazaar.customer.repository.CustomerRepository;
import com.desitech.desibazaar.inventory.service.StockService;  // Assume exists
import com.desitech.desibazaar.shop.entity.Shop;
import com.desitech.desibazaar.shop.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SaleService {
    @Autowired
    private SaleRepository saleRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private StockService stockService;  // For deducting stock
    @Autowired
    private InvoiceService invoiceService;  // For PDF
    @Autowired
    private ChangeLogService changeLogService;

    @Transactional
    public byte[] createSale(SaleDto dto) {
        Shop shop = shopRepository.findById(1L).orElseThrow();  // Single shop
        Optional<Customer> customerOpt = Optional.ofNullable(dto.getCustomerId()).flatMap(customerRepository::findById);
        Customer customer = customerOpt.orElse(null);

        List<SaleItem> saleItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (SaleItemDto itemDto : dto.getItems()) {
            Item item = itemRepository.findById(itemDto.getItemId()).orElseThrow();
            BigDecimal taxableValue = itemDto.getQty().multiply(itemDto.getUnitPrice()).subtract(itemDto.getDiscount());
            int gstRate = item.getGstRate();
            BigDecimal gstAmount = taxableValue.multiply(BigDecimal.valueOf(gstRate).divide(BigDecimal.valueOf(100)));

            SaleItem saleItem = new SaleItem();
            saleItem.setItemId(item.getId());
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
            total = total.add(taxableValue.add(gstAmount));
            // Deduct stock
            stockService.deductStock(item.getId(), itemDto.getQty());
        }

        // Rounding (Indian rules: HALF_UP to 2 decimals)
        BigDecimal roundOff = total.subtract(total.setScale(0, RoundingMode.HALF_UP));
        total = total.setScale(0, RoundingMode.HALF_UP);

        // Generate invoice no: SHOPCODE-YYYYMM-SEQ
        String seq = String.format("%03d", saleRepository.count() + 1);  // Simple seq; use DB sequence for prod
        String invoiceNo = shop.getCode() + "-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM")) + "-" + seq;

        Sale sale = new Sale();
        sale.setInvoiceNo(invoiceNo);
        sale.setShopId(shop.getId());
        sale.setCustomerId(dto.getCustomerId());
        sale.setTotalAmount(total);
        sale.setRoundOff(roundOff.negate());  // If positive round up, store negative adjustment
        sale.setPaymentMethod(dto.getPaymentMethod());
        sale.setSaleItems(saleItems);
        saleItems.forEach(si -> si.setSale(sale));

        saleRepository.save(sale);

        changeLogService.append("SALE", sale.getId(), "CREATE", sale, "LOCAL_DEVICE");

        // Generate PDF
        return invoiceService.generatePdf(sale);
    }
}