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
// REMOVED: Obsolete repository
// import com.desitech.vyaparsathi.inventory.repository.StockEntryRepository;
import com.desitech.vyaparsathi.inventory.repository.StockMovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsService.class);

    @Autowired private SaleRepository saleRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private ItemVariantRepository itemVariantRepository;
    // CHANGED: Use the correct repository for stock information
    @Autowired private StockMovementRepository stockMovementRepository;
    @Autowired private PurchaseOrderItemRepository purchaseOrderItemRepository;

    // 1. Predict item demand (Corrected for performance)
    public List<ItemDemandPredictionDto> predictItemDemand(Long itemId) {
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
        List<Sale> sales = saleRepository.findByDateBetween(threeMonthsAgo, LocalDateTime.now());

        Map<Long, Integer> demandMap = sales.stream()
                .flatMap(sale -> sale.getSaleItems().stream())
                .filter(item -> itemId == null || item.getItemVariant().getId().equals(itemId))
                .collect(Collectors.groupingBy(
                        item -> item.getItemVariant().getId(),
                        Collectors.summingInt(item -> item.getQty().intValue())
                ));

        if (demandMap.isEmpty()) {
            return Collections.emptyList();
        }

        // Fetch all needed variants in one query to avoid N+1
        Map<Long, ItemVariant> variantMap = itemVariantRepository.findAllById(demandMap.keySet()).stream()
                .collect(Collectors.toMap(ItemVariant::getId, Function.identity()));

        List<ItemDemandPredictionDto> result = demandMap.entrySet().stream().map(entry -> {
            ItemVariant variant = variantMap.get(entry.getKey());
            if (variant == null) return null;
            return new ItemDemandPredictionDto(
                    variant.getId(),
                    variant.getItem().getName(),
                    entry.getValue(),
                    "stable" // Placeholder
            );
        }).filter(Objects::nonNull).collect(Collectors.toList());

        logger.info("Predicted item demand for {} items", result.size());
        return result;
    }

    // 2. Customer buying trends (No major change needed, but be aware of performance on large customer sets)
    public List<CustomerTrendDto> getCustomerTrends(Long customerId) {
        // This method's performance depends heavily on the number of customers.
        // For now, it's acceptable, but for thousands of customers, a more optimized query would be needed.
        logger.info("Calculating customer trends for customerId={}", customerId);
        List<Customer> customers = customerId == null ? customerRepository.findAll() :
                customerRepository.findById(customerId).map(List::of).orElse(List.of());

        return customers.stream().map(customer -> {
            List<Sale> sales = saleRepository.findByCustomerId(customer.getId(), org.springframework.data.domain.PageRequest.of(0, 100)).getContent();
            Map<String, Long> itemCount = sales.stream()
                    .flatMap(sale -> sale.getSaleItems().stream())
                    .collect(Collectors.groupingBy(
                            item -> item.getItemVariant().getItem().getName(),
                            Collectors.summingLong(item -> item.getQty().longValue())
                    ));

            List<String> frequentItems = itemCount.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .limit(5)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            String buyingPattern = sales.size() > 10 ? "frequent" : (sales.size() > 0 ? "occasional" : "none");
            return new CustomerTrendDto(customer.getId(), customer.getName(), buyingPattern, frequentItems);
        }).collect(Collectors.toList());
    }

    // 3. Suggest future purchase orders (Corrected for repository and performance)
    public List<PurchaseOrderSuggestionDto> suggestFuturePurchaseOrders() {
        logger.info("Suggesting future purchase orders for low stock items");
        List<ItemVariant> variants = itemVariantRepository.findAll();
        if (variants.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> variantIds = variants.stream().map(ItemVariant::getId).collect(Collectors.toList());

        // Fetch all stock levels in a single query
        Map<Long, BigDecimal> stockMap = stockMovementRepository.findTotalQuantitiesByItemVariantIds(variantIds)
                .stream()
                .collect(Collectors.toMap(
                        StockMovementRepository.StockQuantity::getVariantId,
                        StockMovementRepository.StockQuantity::getTotalQuantity
                ));

        return variants.stream()
                .filter(variant -> variant.getLowStockThreshold() != null)
                .map(variant -> {
                    BigDecimal currentStock = stockMap.getOrDefault(variant.getId(), BigDecimal.ZERO);
                    if (currentStock.compareTo(variant.getLowStockThreshold()) < 0) {
                        BigDecimal suggestedQty = variant.getLowStockThreshold().multiply(BigDecimal.valueOf(1.1)).subtract(currentStock);
                        if (suggestedQty.compareTo(BigDecimal.ZERO) > 0) {
                            return new PurchaseOrderSuggestionDto(
                                    variant.getId(),
                                    variant.getItem().getName(),
                                    suggestedQty.doubleValue()
                            );
                        }
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // 4. Top rising/falling items (Corrected for performance)
    public List<TopItemDto> getTopRisingFallingItems() {
        logger.info("Calculating top rising/falling items");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastMonthStart = now.minusMonths(1);
        LocalDateTime prevMonthStart = now.minusMonths(2);

        Map<Long, Integer> lastMonthMap = getSalesVolumeByVariant(lastMonthStart, now);
        Map<Long, Integer> prevMonthMap = getSalesVolumeByVariant(prevMonthStart, lastMonthStart);

        Set<Long> allVariantIds = new HashSet<>(lastMonthMap.keySet());
        allVariantIds.addAll(prevMonthMap.keySet());

        if (allVariantIds.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, ItemVariant> variantMap = itemVariantRepository.findAllById(allVariantIds).stream()
                .collect(Collectors.toMap(ItemVariant::getId, Function.identity()));

        List<TopItemDto> result = allVariantIds.stream().map(id -> {
            int lastQty = lastMonthMap.getOrDefault(id, 0);
            int prevQty = prevMonthMap.getOrDefault(id, 0);
            if (lastQty == 0 && prevQty == 0) return null;

            // Ensure change is a non-null double primitive
            double change = (prevQty == 0) ? 100.0 : ((double) (lastQty - prevQty) * 100.0 / prevQty);
            ItemVariant variant = variantMap.get(id);
            if (variant != null) {
                return new TopItemDto(id, variant.getItem().getName(), change, change >= 0);
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        // CORRECTED: This sorting is now null-safe and more robust.
        // It sorts by the absolute value of the change percentage in descending order.
        result.sort(Comparator.comparing(
                dto -> Math.abs(dto.getChangePercent()),
                Comparator.reverseOrder()
        ));

        return result.stream().limit(10).collect(Collectors.toList());
    }
    private Map<Long, Integer> getSalesVolumeByVariant(LocalDateTime start, LocalDateTime end) {
        return saleRepository.findByDateBetween(start, end).stream()
                .flatMap(sale -> sale.getSaleItems().stream())
                .collect(Collectors.groupingBy(
                        item -> item.getItemVariant().getId(),
                        Collectors.summingInt(item -> item.getQty().intValue())
                ));
    }

    // 5. Seasonal trends (No change needed)
    public List<SeasonalTrendDto> getSeasonalTrends() {
        logger.info("Calculating seasonal sales trends");
        Map<Integer, Long> monthSales = saleRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        sale -> sale.getDate().getMonthValue(),
                        Collectors.counting()
                ));

        List<SeasonalTrendDto> result = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            long count = monthSales.getOrDefault(month, 0L);
            String season = (month == 12 || month <= 2) ? "Winter" : (month <= 5 ? "Spring" : (month <= 8 ? "Summer" : "Autumn"));
            result.add(new SeasonalTrendDto(season + " (Month " + month + ")", "Sales: " + count));
        }
        return result;
    }

    // 6. Churn prediction (Be aware of performance on large customer sets)
    public List<ChurnPredictionDto> predictChurn() {
        logger.info("Predicting customer churn");
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
        List<Customer> customers = customerRepository.findAll();

        return customers.stream().map(customer -> {
            // This still queries one by one. For high performance, a custom repository method would be better.
            // e.g., "findLastSaleDateForCustomerIds(List<Long> customerIds)"
            Optional<Sale> lastSale = saleRepository.findTopByCustomerIdOrderByDateDesc(customer.getId());

            boolean isChurnRisk = lastSale.isEmpty() || lastSale.get().getDate().isBefore(threeMonthsAgo);
            double probability = isChurnRisk ? 0.9 : 0.1;

            return new ChurnPredictionDto(customer.getId(), customer.getName(), probability);
        }).collect(Collectors.toList());
    }
}