package com.desitech.vyaparsathi.product.service;

import com.desitech.vyaparsathi.catalog.entity.ItemVariant;
import com.desitech.vyaparsathi.inventory.entity.StockEntry;
import com.desitech.vyaparsathi.product.dto.ProductDto;
import com.desitech.vyaparsathi.catalog.repository.ItemVariantRepository;
import com.desitech.vyaparsathi.inventory.repository.StockEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ItemVariantRepository itemVariantRepository;
    private final StockEntryRepository stockEntryRepository;

    public List<ProductDto> getAllProducts() {
        List<ItemVariant> variants = itemVariantRepository.findAll();

        return variants.stream().map(variant -> {
            ProductDto dto = new ProductDto();
            dto.setItemVariantId(variant.getId());
            dto.setItemName(variant.getItem().getName());
            dto.setDescription(variant.getItem().getDescription());
            dto.setSku(variant.getSku());
            dto.setColor(variant.getColor());
            dto.setSize(variant.getSize());
            dto.setDesign(variant.getDesign());
            dto.setPricePerUnit(variant.getPricePerUnit());
            dto.setPhotoPath(variant.getPhotoPath());

            // fetch all stock entries for this variant
            List<StockEntry> stockList = stockEntryRepository.findByItemVariantId(variant.getId());

            // calculate total quantity
            BigDecimal totalQuantity = stockList.stream()
                    .map(StockEntry::getQuantity)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            dto.setAvailableQuantity(totalQuantity);

            // combine batch info
            String batchInfo = stockList.stream()
                    .map(StockEntry::getBatch)
                    .filter(b -> b != null && !b.isEmpty())
                    .collect(Collectors.joining(", "));
            dto.setBatch(batchInfo);

            return dto;
        }).collect(Collectors.toList());
    }
}
