package com.desitech.desibazaar.inventory.service;

import com.desitech.desibazaar.catalog.entity.Item;
import com.desitech.desibazaar.catalog.repository.ItemRepository;
import com.desitech.desibazaar.inventory.dto.CurrentStockDto;
import com.desitech.desibazaar.inventory.dto.StockAddDto;
import com.desitech.desibazaar.inventory.dto.StockEntryDto;
import com.desitech.desibazaar.inventory.entity.StockEntry;
import com.desitech.desibazaar.inventory.mapper.StockEntryMapper;
import com.desitech.desibazaar.inventory.repository.StockEntryRepository;
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
    private ItemRepository itemRepository;

    @Autowired
    private StockEntryMapper mapper;

    public StockEntryDto addStock(StockAddDto dto) {
        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new RuntimeException("Item not found"));

        StockEntry entry = new StockEntry();
        entry.setItemId(dto.getItemId());
        entry.setQuantity(dto.getQuantity());
        entry.setBatch(dto.getBatch());

        stockEntryRepository.save(entry);
        return mapper.toDto(entry);
    }

    public List<CurrentStockDto> getCurrentStock() {
        // Assuming we want stock for all items; could add paging or filters
        List<Item> items = itemRepository.findAll();
        return items.stream().map(item -> {
            BigDecimal total = stockEntryRepository.getTotalQuantityByItemId(item.getId());
            if (total == null) {
                total = BigDecimal.ZERO;
            }
            CurrentStockDto dto = new CurrentStockDto();
            dto.setItemId(item.getId());
            dto.setTotalQuantity(total);
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public void deductStock(Long itemId, BigDecimal quantityToDeduct) {
        BigDecimal currentTotal = stockEntryRepository.getTotalQuantityByItemId(itemId);
        if (currentTotal == null || currentTotal.compareTo(quantityToDeduct) < 0) {
            throw new RuntimeException("Insufficient stock for item " + itemId);
        }

        // Simple deduction: find entries and deduct FIFO style
        List<StockEntry> entries = stockEntryRepository.findByItemId(itemId)
                .stream()
                .sorted((e1, e2) -> e1.getLastUpdated().compareTo(e2.getLastUpdated())) // Oldest first
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
}