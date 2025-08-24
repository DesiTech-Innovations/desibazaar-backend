package com.desitech.vyaparsathi.analytics.service;

import com.desitech.vyaparsathi.common.exception.EntityNotFoundAppException;
import com.desitech.vyaparsathi.purchaseorder.repository.PurchaseOrderItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desitech.vyaparsathi.analytics.dto.*;
import com.desitech.vyaparsathi.sales.entity.Sale;
import com.desitech.vyaparsathi.sales.entity.SaleItem;
import com.desitech.vyaparsathi.sales.repository.SaleRepository;
import com.desitech.vyaparsathi.customer.entity.Customer;
import com.desitech.vyaparsathi.customer.repository.CustomerRepository;
import com.desitech.vyaparsathi.inventory.entity.ItemVariant;
import com.desitech.vyaparsathi.inventory.repository.ItemVariantRepository;
import com.desitech.vyaparsathi.inventory.repository.StockEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsService.class);

    @Autowired
    private SaleRepository saleRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ItemVariantRepository itemVariantRepository;
    @Autowired
    private StockEntryRepository stockEntryRepository;
    @Autowired
    private PurchaseOrderItemRepository purchaseOrderItemRepository;

    // 1. Predict item demand based on sales in the last 3 months
    public List<ItemDemandPredictionDto> predictItemDemand(Long itemId) {
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
        List<Sale> sales = saleRepository.findByDateBetween(threeMonthsAgo, LocalDateTime.now());
        Map<Long, Integer> demandMap = new HashMap<>();
        for (Sale sale : sales) {
            for (SaleItem item : sale.getSaleItems()) {
                Long variantId = item.getItemVariant().getId();
                if (itemId == null || variantId.equals(itemId)) {
                    demandMap.put(variantId, demandMap.getOrDefault(variantId, 0) + item.getQty().intValue());
                }
            }
        }
        List<ItemDemandPredictionDto> result = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : demandMap.entrySet()) {
            ItemVariant variant = itemVariantRepository.findById(entry.getKey())
                    .orElseThrow(() -> new EntityNotFoundAppException("ItemVariant", entry.getKey()));
            result.add(new ItemDemandPredictionDto(
                    variant.getId(),
                    variant.getItem().getName(),
                    entry.getValue(),
                    "stable" // Placeholder, can be improved with trend analysis
            ));
        }
        logger.info("Predicted item demand for {} items", result.size());
        return result;
    }

    // 2. Customer buying trends (most frequent items, buying pattern)
    public List<CustomerTrendDto> getCustomerTrends(Long customerId) {
    logger.info("Calculating customer trends for customerId={}", customerId);
        List<Customer> customers = customerId == null ? customerRepository.findAll() :
                customerRepository.findById(customerId).map(List::of).orElse(List.of());
        List<CustomerTrendDto> result = new ArrayList<>();
        for (Customer customer : customers) {
            List<Sale> sales = saleRepository.findByCustomerId(customer.getId(), org.springframework.data.domain.PageRequest.of(0, 100)).getContent();
            Map<String, Long> itemCount = new HashMap<>();
            for (Sale sale : sales) {
                for (SaleItem item : sale.getSaleItems()) {
                    String itemName = item.getItemVariant().getItem().getName();
                    itemCount.put(itemName, itemCount.getOrDefault(itemName, 0L) + item.getQty().longValue());
                }
            }
            List<String> frequentItems = itemCount.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .limit(5)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            String buyingPattern = sales.size() > 10 ? "frequent" : (sales.size() > 0 ? "occasional" : "none");
            result.add(new CustomerTrendDto(
                    customer.getId(),
                    customer.getName(),
                    buyingPattern,
                    frequentItems
            ));
        }
        return result;
    }

    // 3. Suggest future purchase orders (low stock items)
    public List<PurchaseOrderSuggestionDto> suggestFuturePurchaseOrders() {
    logger.info("Suggesting future purchase orders for low stock items");
        List<ItemVariant> variants = itemVariantRepository.findAll();
        List<PurchaseOrderSuggestionDto> result = new ArrayList<>();
        for (ItemVariant variant : variants) {
            BigDecimal totalStock = stockEntryRepository.getTotalQuantityByItemVariantId(variant.getId());
            if (variant.getLowStockThreshold() != null && totalStock != null && totalStock.compareTo(variant.getLowStockThreshold()) < 0) {
                // Suggest to order up to threshold + 10%
                BigDecimal suggestedQty = variant.getLowStockThreshold().multiply(BigDecimal.valueOf(1.1)).subtract(totalStock);
                result.add(new PurchaseOrderSuggestionDto(
                        variant.getId(),
                        variant.getItem().getName(),
                        suggestedQty.doubleValue()
                ));
            }
        }
        return result;
    }

    // 4. Top rising/falling items (compare sales in last month vs previous month)
    public List<TopItemDto> getTopRisingFallingItems() {
    logger.info("Calculating top rising/falling items");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastMonth = now.minusMonths(1);
        LocalDateTime prevMonth = now.minusMonths(2);
        List<Sale> salesLastMonth = saleRepository.findByDateBetween(lastMonth, now);
        List<Sale> salesPrevMonth = saleRepository.findByDateBetween(prevMonth, lastMonth);
        Map<Long, Integer> lastMonthMap = new HashMap<>();
        Map<Long, Integer> prevMonthMap = new HashMap<>();
        for (Sale sale : salesLastMonth) {
            for (SaleItem item : sale.getSaleItems()) {
                Long id = item.getItemVariant().getId();
                lastMonthMap.put(id, lastMonthMap.getOrDefault(id, 0) + item.getQty().intValue());
            }
        }
        for (Sale sale : salesPrevMonth) {
            for (SaleItem item : sale.getSaleItems()) {
                Long id = item.getItemVariant().getId();
                prevMonthMap.put(id, prevMonthMap.getOrDefault(id, 0) + item.getQty().intValue());
            }
        }
        List<TopItemDto> result = new ArrayList<>();
        for (Long id : lastMonthMap.keySet()) {
            int lastQty = lastMonthMap.getOrDefault(id, 0);
            int prevQty = prevMonthMap.getOrDefault(id, 0);
            double change = prevQty == 0 ? 100.0 : ((lastQty - prevQty) * 100.0 / prevQty);
            ItemVariant variant = itemVariantRepository.findById(id).orElse(null);
            if (variant != null) {
                result.add(new TopItemDto(
                        id,
                        variant.getItem().getName(),
                        change,
                        change >= 0
                ));
            }
        }
        // Sort by absolute change percent, descending
        result.sort((a, b) -> Double.compare(Math.abs(b.getChangePercent()), Math.abs(a.getChangePercent())));
        return result.stream().limit(10).collect(Collectors.toList());
    }

    // 5. Seasonal trends (sales grouped by month/season)
    public List<SeasonalTrendDto> getSeasonalTrends() {
    logger.info("Calculating seasonal sales trends");
        List<Sale> sales = saleRepository.findAll();
        Map<Integer, Integer> monthSales = new HashMap<>();
        for (Sale sale : sales) {
            int month = sale.getDate().getMonthValue();
            monthSales.put(month, monthSales.getOrDefault(month, 0) + 1);
        }
        List<SeasonalTrendDto> result = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            int count = monthSales.getOrDefault(month, 0);
            String season = (month == 12 || month <= 2) ? "Winter" : (month <= 5 ? "Spring" : (month <= 8 ? "Summer" : "Autumn"));
            result.add(new SeasonalTrendDto(season + " (Month " + month + ")", "Sales: " + count));
        }
        return result;
    }

    // 6. Churn prediction (customers with no purchase in last 3 months)
    public List<ChurnPredictionDto> predictChurn() {
    logger.info("Predicting customer churn");
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
        List<Customer> customers = customerRepository.findAll();
        List<ChurnPredictionDto> result = new ArrayList<>();
        for (Customer customer : customers) {
            List<Sale> sales = saleRepository.findByCustomerId(customer.getId(), org.springframework.data.domain.PageRequest.of(0, 1)).getContent();
            boolean churned = sales.isEmpty() || sales.get(0).getDate().isBefore(threeMonthsAgo);
            double probability = churned ? 0.9 : 0.1;
            result.add(new ChurnPredictionDto(
                    customer.getId(),
                    customer.getName(),
                    probability
            ));
        }
        return result;
    }
}