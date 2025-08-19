package com.desitech.vyaparsathi.reports.service;

import com.desitech.vyaparsathi.inventory.entity.PurchaseOrderItem;
import com.desitech.vyaparsathi.inventory.repository.PurchaseOrderItemRepository;
import com.desitech.vyaparsathi.sales.entity.Sale;
import com.desitech.vyaparsathi.sales.entity.SaleItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Service to calculate Cost of Goods Sold (COGS) based on purchase costs and sales data.
 * Uses average cost method to calculate COGS for sold items.
 */
@Service
public class COGSCalculationService {

    @Autowired
    private PurchaseOrderItemRepository purchaseOrderItemRepository;

    /**
     * Calculate COGS for sold items in a date range.
     * Uses average cost method - calculates average purchase cost per item variant.
     * 
     * @param sales List of sales in the date range
     * @return Total COGS for the period
     */
    public BigDecimal calculateCOGS(List<Sale> sales) {
        BigDecimal totalCOGS = BigDecimal.ZERO;
        
        for (Sale sale : sales) {
            for (SaleItem saleItem : sale.getSaleItems()) {
                BigDecimal itemCOGS = calculateItemCOGS(saleItem);
                totalCOGS = totalCOGS.add(itemCOGS);
            }
        }
        
        return totalCOGS;
    }

    /**
     * Calculate COGS for a specific sale item using average cost method.
     * 
     * @param saleItem The sale item to calculate COGS for
     * @return COGS for this specific item
     */
    public BigDecimal calculateItemCOGS(SaleItem saleItem) {
        Long itemVariantId = saleItem.getItemVariant().getId();
        BigDecimal quantitySold = saleItem.getQty();
        
        // Get average purchase cost for this item variant
        BigDecimal averageCost = calculateAveragePurchaseCost(itemVariantId);
        
        // COGS = Quantity Sold × Average Cost
        return quantitySold.multiply(averageCost);
    }

    /**
     * Calculate average purchase cost for an item variant across all purchase orders.
     * 
     * @param itemVariantId The item variant ID
     * @return Average purchase cost per unit
     */
    public BigDecimal calculateAveragePurchaseCost(Long itemVariantId) {
        List<PurchaseOrderItem> purchaseItems = purchaseOrderItemRepository.findByItemVariantId(itemVariantId);
        
        if (purchaseItems.isEmpty()) {
            // If no purchase data available, return zero (or could return selling price)
            return BigDecimal.ZERO;
        }
        
        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal totalQuantity = BigDecimal.ZERO;
        
        for (PurchaseOrderItem purchaseItem : purchaseItems) {
            BigDecimal itemTotalCost = purchaseItem.getUnitCost().multiply(BigDecimal.valueOf(purchaseItem.getQuantity()));
            totalCost = totalCost.add(itemTotalCost);
            totalQuantity = totalQuantity.add(BigDecimal.valueOf(purchaseItem.getQuantity()));
        }
        
        if (totalQuantity.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        // Return average cost = Total Cost / Total Quantity
        return totalCost.divide(totalQuantity, 2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Calculate COGS for sales within a specific date range
     * 
     * @param sales Sales within the date range
     * @param fromDate Start date (for logging/tracking purposes)
     * @param toDate End date (for logging/tracking purposes) 
     * @return Total COGS for the period
     */
    public BigDecimal calculateCOGSForPeriod(List<Sale> sales, LocalDate fromDate, LocalDate toDate) {
        return calculateCOGS(sales);
    }
}