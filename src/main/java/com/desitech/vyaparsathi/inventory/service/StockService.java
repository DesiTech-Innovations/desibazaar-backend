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
        entry.setBatch(dto.getBatch());

        stockEntryRepository.save(entry);
        return mapper.toDto(entry);
    }

    // New method for adding stock from a purchase order
    @Transactional
    public void addStock(Long itemVariantId, BigDecimal quantity) {
        ItemVariant itemVariant = itemVariantRepository.findById(itemVariantId)
                .orElseThrow(() -> new RuntimeException("Item Variant not found"));

        StockEntry entry = new StockEntry();
        entry.setItemVariant(itemVariant);
        entry.setQuantity(quantity);
        entry.setBatch("Purchase Order"); // Hardcoded batch for simplicity

        stockEntryRepository.save(entry);
    }

    public List<CurrentStockDto> getCurrentStock() {
        List<ItemVariant> itemVariants = itemVariantRepository.findAll();
        return itemVariants.stream().map(variant -> {
            BigDecimal total = stockEntryRepository.getTotalQuantityByItemVariantId(variant.getId());
            if (total == null) {
                total = BigDecimal.ZERO;
            }
            CurrentStockDto dto = new CurrentStockDto();
            dto.setItemVariantId(variant.getId());
            dto.setTotalQuantity(total);
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
                .sorted((e1, e2) -> e1.getLastUpdated().compareTo(e2.getLastUpdated()))
                .collect(Collectors.toList());

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
}