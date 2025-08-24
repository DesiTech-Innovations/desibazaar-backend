package com.desitech.vyaparsathi.reports.service;

import com.desitech.vyaparsathi.inventory.entity.Item;
import com.desitech.vyaparsathi.inventory.entity.ItemVariant;
import com.desitech.vyaparsathi.purchaseorder.entity.PurchaseOrderItem;
import com.desitech.vyaparsathi.purchaseorder.repository.PurchaseOrderItemRepository;
import com.desitech.vyaparsathi.sales.entity.Sale;
import com.desitech.vyaparsathi.sales.entity.SaleItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class COGSCalculationServiceTest {

    @Mock
    private PurchaseOrderItemRepository purchaseOrderItemRepository;

    @InjectMocks
    private COGSCalculationService cogsCalculationService;

    private ItemVariant itemVariant;
    private Item item;
    private Sale sale;
    private SaleItem saleItem;

    @BeforeEach
    void setUp() {
        // Setup test entities
        item = new Item();
        item.setId(1L);
        item.setName("Test Item");

        itemVariant = new ItemVariant();
        itemVariant.setId(1L);
        itemVariant.setSku("TEST-001");
        itemVariant.setPricePerUnit(new BigDecimal("100.00"));
        itemVariant.setItem(item);

        sale = new Sale();
        sale.setId(1L);
        sale.setDate(LocalDateTime.now());

        saleItem = new SaleItem();
        saleItem.setId(1L);
        saleItem.setItemVariant(itemVariant);
        saleItem.setQty(new BigDecimal("5.0"));
        saleItem.setUnitPrice(new BigDecimal("100.00"));
        saleItem.setSale(sale);

        sale.setSaleItems(Arrays.asList(saleItem));
    }

    @Test
    @DisplayName("Should calculate average purchase cost correctly")
    void shouldCalculateAveragePurchaseCostCorrectly() {
        // Setup purchase order items with different costs
        PurchaseOrderItem poi1 = createPurchaseOrderItem(new BigDecimal("60.00"), 10);
        PurchaseOrderItem poi2 = createPurchaseOrderItem(new BigDecimal("80.00"), 20);
        
        when(purchaseOrderItemRepository.findByItemVariantId(1L))
            .thenReturn(Arrays.asList(poi1, poi2));

        BigDecimal averageCost = cogsCalculationService.calculateAveragePurchaseCost(1L);
        
        // Expected: (60*10 + 80*20) / (10+20) = (600 + 1600) / 30 = 2200 / 30 = 73.33
        assertEquals(new BigDecimal("73.33"), averageCost);
    }

    @Test
    @DisplayName("Should return zero when no purchase data available")
    void shouldReturnZeroWhenNoPurchaseData() {
        when(purchaseOrderItemRepository.findByItemVariantId(anyLong()))
            .thenReturn(Collections.emptyList());

        BigDecimal averageCost = cogsCalculationService.calculateAveragePurchaseCost(1L);
        assertEquals(BigDecimal.ZERO, averageCost);
    }

    @Test
    @DisplayName("Should calculate item COGS correctly")
    void shouldCalculateItemCOGSCorrectly() {
        PurchaseOrderItem poi = createPurchaseOrderItem(new BigDecimal("70.00"), 50);
        
        when(purchaseOrderItemRepository.findByItemVariantId(1L))
            .thenReturn(Arrays.asList(poi));

        BigDecimal itemCOGS = cogsCalculationService.calculateItemCOGS(saleItem);
        
        // Expected: quantity sold (5) * average cost (70) = 350
        assertEquals(0, new BigDecimal("350.00").compareTo(itemCOGS));
    }

    @Test
    @DisplayName("Should calculate total COGS for multiple sales")
    void shouldCalculateTotalCOGSForMultipleSales() {
        // Setup another sale item
        SaleItem saleItem2 = new SaleItem();
        saleItem2.setId(2L);
        saleItem2.setItemVariant(itemVariant);
        saleItem2.setQty(new BigDecimal("3.0"));
        saleItem2.setSale(sale);

        Sale sale2 = new Sale();
        sale2.setId(2L);
        sale2.setSaleItems(Arrays.asList(saleItem2));

        List<Sale> sales = Arrays.asList(sale, sale2);

        PurchaseOrderItem poi = createPurchaseOrderItem(new BigDecimal("50.00"), 100);
        
        when(purchaseOrderItemRepository.findByItemVariantId(1L))
            .thenReturn(Arrays.asList(poi));

        BigDecimal totalCOGS = cogsCalculationService.calculateCOGS(sales);
        
        // Expected: (5 * 50) + (3 * 50) = 250 + 150 = 400
        assertEquals(0, new BigDecimal("400.00").compareTo(totalCOGS));
    }

    @Test
    @DisplayName("Should handle zero quantity in purchase orders")
    void shouldHandleZeroQuantityInPurchaseOrders() {
        PurchaseOrderItem poi = createPurchaseOrderItem(new BigDecimal("50.00"), 0);
        
        when(purchaseOrderItemRepository.findByItemVariantId(1L))
            .thenReturn(Arrays.asList(poi));

        BigDecimal averageCost = cogsCalculationService.calculateAveragePurchaseCost(1L);
        assertEquals(BigDecimal.ZERO, averageCost);
    }

    private PurchaseOrderItem createPurchaseOrderItem(BigDecimal unitCost, int quantity) {
        PurchaseOrderItem poi = new PurchaseOrderItem();
        poi.setItemVariant(itemVariant);
        poi.setUnitCost(unitCost);
        poi.setQuantity(quantity);
        return poi;
    }
}