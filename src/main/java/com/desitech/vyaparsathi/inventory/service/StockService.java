package com.desitech.vyaparsathi.inventory.service;

import com.desitech.vyaparsathi.catalog.entity.ItemVariant;
import com.desitech.vyaparsathi.catalog.repository.ItemVariantRepository;
import com.desitech.vyaparsathi.inventory.dto.*;
import com.desitech.vyaparsathi.inventory.entity.StockEntry;
import com.desitech.vyaparsathi.inventory.entity.StockMovement;
import com.desitech.vyaparsathi.inventory.mapper.StockEntryMapper;
import com.desitech.vyaparsathi.inventory.repository.StockEntryRepository;
import com.desitech.vyaparsathi.inventory.repository.StockMovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    @Autowired
    private StockMovementRepository stockMovementRepository;

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
        
        // Record stock movement
        recordStockMovement(dto.getItemVariantId(), "ADD", dto.getQuantity(), 
                          entry.getCostPerUnit(), dto.getBatch(), "Manual Stock Addition", "Manual Entry");
        
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
        
        // Record stock movement
        recordStockMovement(itemVariantId, "ADD", quantity, entry.getCostPerUnit(), 
                          entry.getBatch(), "Stock Added", batch);
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
        deductStock(itemVariantId, quantityToDeduct, "Stock Deduction", "Stock Operation");
    }

    @Transactional
    public void deductStock(Long itemVariantId, BigDecimal quantityToDeduct, String reason, String reference) {
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

        // Record stock movement
        recordStockMovement(itemVariantId, "DEDUCT", quantityToDeduct.negate(), 
                          BigDecimal.ZERO, null, reason, reference);
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

    /**
     * Record stock movement for tracking purposes
     */
    private void recordStockMovement(Long itemVariantId, String movementType, BigDecimal quantity, 
                                   BigDecimal costPerUnit, String batch, String reason, String reference) {
        ItemVariant itemVariant = itemVariantRepository.findById(itemVariantId)
                .orElseThrow(() -> new RuntimeException("Item Variant not found"));

        StockMovement movement = new StockMovement();
        movement.setItemVariant(itemVariant);
        movement.setMovementType(movementType);
        movement.setQuantity(quantity);
        movement.setCostPerUnit(costPerUnit);
        movement.setBatch(batch);
        movement.setReason(reason);
        movement.setReference(reference);
        movement.setTimestamp(LocalDateTime.now());

        stockMovementRepository.save(movement);
    }

    /**
     * Get stock movement history for an item variant
     */
    public List<StockMovementDto> getStockMovements(Long itemVariantId) {
        List<StockMovement> movements = stockMovementRepository.findByItemVariantIdOrderByTimestampDesc(itemVariantId);
        return movements.stream().map(this::mapToStockMovementDto).collect(Collectors.toList());
    }

    /**
     * Get stock movements within date range
     */
    public List<StockMovementDto> getStockMovements(LocalDateTime startDate, LocalDateTime endDate) {
        List<StockMovement> movements = stockMovementRepository.findByTimestampBetweenOrderByTimestampDesc(startDate, endDate);
        return movements.stream().map(this::mapToStockMovementDto).collect(Collectors.toList());
    }

    /**
     * Get low stock alerts
     */
    public List<LowStockAlertDto> getLowStockAlerts() {
        List<ItemVariant> itemVariants = itemVariantRepository.findAll();
        
        return itemVariants.stream()
                .filter(variant -> variant.getLowStockThreshold() != null && variant.getLowStockThreshold().compareTo(BigDecimal.ZERO) > 0)
                .map(variant -> {
                    BigDecimal currentStock = getCurrentStock(variant.getId());
                    if (currentStock == null) currentStock = BigDecimal.ZERO;
                    
                    if (currentStock.compareTo(variant.getLowStockThreshold()) <= 0) {
                        LowStockAlertDto alert = new LowStockAlertDto();
                        alert.setItemVariantId(variant.getId());
                        alert.setItemName(variant.getItem().getName());
                        alert.setSku(variant.getSku());
                        alert.setCurrentStock(currentStock);
                        alert.setThreshold(variant.getLowStockThreshold());
                        alert.setUnit(variant.getUnit());
                        alert.setAlertLevel(currentStock.compareTo(BigDecimal.ZERO) == 0 ? "CRITICAL" : "LOW");
                        return alert;
                    }
                    return null;
                })
                .filter(alert -> alert != null)
                .collect(Collectors.toList());
    }

    /**
     * Manual stock adjustment
     */
    @Transactional
    public StockEntryDto adjustStock(StockAdjustmentDto dto) {
        if (dto.getReason() == null || dto.getReason().trim().isEmpty()) {
            throw new IllegalArgumentException("Reason is required for stock adjustment");
        }

        ItemVariant itemVariant = itemVariantRepository.findById(dto.getItemVariantId())
                .orElseThrow(() -> new RuntimeException("Item Variant not found"));

        // Record the movement first
        recordStockMovement(dto.getItemVariantId(), "ADJUST", dto.getAdjustmentQuantity(), 
                          dto.getCostPerUnit(), dto.getBatch(), dto.getReason(), "Manual Adjustment");

        if (dto.getAdjustmentQuantity().compareTo(BigDecimal.ZERO) > 0) {
            // Positive adjustment - add stock
            StockEntry entry = new StockEntry();
            entry.setItemVariant(itemVariant);
            entry.setQuantity(dto.getAdjustmentQuantity());
            entry.setCostPerUnit(dto.getCostPerUnit() != null ? dto.getCostPerUnit() : BigDecimal.ZERO);
            entry.setBatch(dto.getBatch() != null ? dto.getBatch() : "Stock Adjustment");
            
            stockEntryRepository.save(entry);
            return mapper.toDto(entry);
        } else {
            // Negative adjustment - deduct stock
            BigDecimal quantityToDeduct = dto.getAdjustmentQuantity().abs();
            deductStock(dto.getItemVariantId(), quantityToDeduct);
            return null; // No new stock entry created
        }
    }

    private StockMovementDto mapToStockMovementDto(StockMovement movement) {
        StockMovementDto dto = new StockMovementDto();
        dto.setId(movement.getId());
        dto.setItemVariantId(movement.getItemVariant().getId());
        dto.setItemName(movement.getItemVariant().getItem().getName());
        dto.setSku(movement.getItemVariant().getSku());
        dto.setMovementType(movement.getMovementType());
        dto.setQuantity(movement.getQuantity());
        dto.setCostPerUnit(movement.getCostPerUnit());
        dto.setBatch(movement.getBatch());
        dto.setReason(movement.getReason());
        dto.setReference(movement.getReference());
        dto.setTimestamp(movement.getTimestamp());
        return dto;
    }
}