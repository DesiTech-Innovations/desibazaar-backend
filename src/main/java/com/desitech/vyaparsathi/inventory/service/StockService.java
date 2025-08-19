package com.desitech.vyaparsathi.inventory.service;

import com.desitech.vyaparsathi.catalog.entity.ItemVariant;
import com.desitech.vyaparsathi.catalog.repository.ItemVariantRepository;
import com.desitech.vyaparsathi.inventory.dto.CurrentStockDto;
import com.desitech.vyaparsathi.inventory.dto.StockAddDto;
import com.desitech.vyaparsathi.inventory.dto.StockEntryDto;
import com.desitech.vyaparsathi.inventory.entity.StockEntry;
import com.desitech.vyaparsathi.inventory.mapper.StockEntryMapper;
import com.desitech.vyaparsathi.inventory.repository.StockEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockService {

    @Autowired
    private StockEntryRepository stockEntryRepository;

    @Autowired
    private ItemVariantRepository itemVariantRepository;

    @Autowired
    private StockEntryMapper mapper;

    @Transactional
    public StockEntryDto addStockFromDto(StockAddDto dto) {
        ItemVariant itemVariant = itemVariantRepository.findById(dto.getItemVariantId())
                .orElseThrow(() -> new RuntimeException("Item Variant not found"));

        StockEntry entry = new StockEntry();
        entry.setItemVariant(itemVariant);
        entry.setQuantity(dto.getQuantity());
        entry.setCostPerUnit(dto.getCostPerUnit() != null ? dto.getCostPerUnit() : BigDecimal.ZERO);
        entry.setBatch(dto.getBatch());

        stockEntryRepository.save(entry);
        return mapper.toDto(entry);
    }

    // New method for adding stock from a purchase order
    @Transactional
    public void addStock(Long itemVariantId, BigDecimal quantity) {
        addStock(itemVariantId, quantity, BigDecimal.ZERO, "Purchase Order");
    }

    // Overloaded method for adding stock with cost
    @Transactional
    public void addStock(Long itemVariantId, BigDecimal quantity, BigDecimal costPerUnit, String batch) {
        ItemVariant itemVariant = itemVariantRepository.findById(itemVariantId)
                .orElseThrow(() -> new RuntimeException("Item Variant not found"));

        StockEntry entry = new StockEntry();
        entry.setItemVariant(itemVariant);
        entry.setQuantity(quantity);
        entry.setCostPerUnit(costPerUnit != null ? costPerUnit : BigDecimal.ZERO);
        entry.setBatch(batch != null ? batch : "Manual Entry");

        stockEntryRepository.save(entry);
    }

    public List<CurrentStockDto> getCurrentStock() {
        List<ItemVariant> itemVariants = itemVariantRepository.findAll();

        return itemVariants.stream().map(variant -> {
            BigDecimal totalQty = stockEntryRepository.getTotalQuantityByItemVariantId(variant.getId());
            if (totalQty == null) totalQty = BigDecimal.ZERO;

            CurrentStockDto dto = new CurrentStockDto();
            dto.setItemVariantId(variant.getId());
            dto.setItemName(variant.getItem().getName());
            dto.setSku(variant.getSku());
            dto.setUnit(variant.getUnit());
            dto.setColor(variant.getColor());
            dto.setSize(variant.getSize());
            dto.setDesign(variant.getDesign());
            dto.setPricePerUnit(variant.getPricePerUnit());
            dto.setTotalQuantity(totalQty);

            // Optional: If you want batch info, you can fetch the latest batch or multiple batches
            // For simplicity, we'll skip batch aggregation here

            List<StockEntry> stockEntries = stockEntryRepository.findByItemVariantIdOrderByLastUpdatedDesc(variant.getId());
            if (!stockEntries.isEmpty()) {
                dto.setBatch(stockEntries.get(0).getBatch());
            }
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public void deductStock(Long itemVariantId, BigDecimal quantityToDeduct) {
        BigDecimal currentTotal = stockEntryRepository.getTotalQuantityByItemVariantId(itemVariantId);
        if (currentTotal == null || currentTotal.compareTo(quantityToDeduct) < 0) {
            throw new RuntimeException("Insufficient stock for item variant " + itemVariantId);
        }

        List<StockEntry> entries = stockEntryRepository.findByItemVariantId(itemVariantId)
                .stream()
                .sorted((e1, e2) -> {
                    LocalDateTime date1 = e1.getLastUpdated() != null ? e1.getLastUpdated() : LocalDateTime.MAX;
                    LocalDateTime date2 = e2.getLastUpdated() != null ? e2.getLastUpdated() : LocalDateTime.MAX;
                    return date1.compareTo(date2);
                })
                .toList();

        BigDecimal remaining = quantityToDeduct;
        for (StockEntry entry : entries) {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;

            BigDecimal deduct = remaining.min(entry.getQuantity());
            entry.setQuantity(entry.getQuantity().subtract(deduct));
            remaining = remaining.subtract(deduct);

            if (entry.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                stockEntryRepository.delete(entry);
            } else {
                stockEntryRepository.save(entry);
            }
        }
    }

    public boolean isStockAvailable(Long itemVariantId, BigDecimal quantity) {
        BigDecimal currentTotal = stockEntryRepository.getTotalQuantityByItemVariantId(itemVariantId);
        return currentTotal != null && currentTotal.compareTo(quantity) >= 0;
    }

    public BigDecimal getCurrentStock(Long itemVariantId) {
        return stockEntryRepository.getTotalQuantityByItemVariantId(itemVariantId);
    }

    /**
     * Calculate COGS for a given quantity using FIFO method
     * Returns the total cost of goods sold for the specified quantity
     */
    public BigDecimal calculateCOGSFifo(Long itemVariantId, BigDecimal quantityToSell) {
        List<StockEntry> entries = stockEntryRepository.findByItemVariantId(itemVariantId)
                .stream()
                .sorted((e1, e2) -> {
                    LocalDateTime date1 = e1.getLastUpdated() != null ? e1.getLastUpdated() : LocalDateTime.MIN;
                    LocalDateTime date2 = e2.getLastUpdated() != null ? e2.getLastUpdated() : LocalDateTime.MIN;
                    return date1.compareTo(date2); // FIFO: oldest first
                })
                .toList();

        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal remaining = quantityToSell;

        for (StockEntry entry : entries) {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;

            BigDecimal availableQty = entry.getQuantity();
            if (availableQty.compareTo(BigDecimal.ZERO) <= 0) continue;

            BigDecimal qtyToUse = remaining.min(availableQty);
            BigDecimal costForThisEntry = entry.getCostPerUnit().multiply(qtyToUse);
            
            totalCost = totalCost.add(costForThisEntry);
            remaining = remaining.subtract(qtyToUse);
        }

        return totalCost;
    }
}