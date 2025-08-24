package com.desitech.vyaparsathi.inventory.service;


import com.desitech.vyaparsathi.inventory.entity.ItemVariant;
import com.desitech.vyaparsathi.inventory.mapper.ItemMapper;
import com.desitech.vyaparsathi.inventory.repository.ItemVariantRepository;
import com.desitech.vyaparsathi.inventory.repository.ItemRepository;
import com.desitech.vyaparsathi.inventory.dto.ItemVariantDto;
import com.desitech.vyaparsathi.inventory.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemVariantService {

    @Autowired
    private ItemVariantRepository itemVariantRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemMapper mapper;

    @Autowired
    private StockService stockService;

    @Transactional
    public ItemVariantDto create(ItemVariantDto dto) {
        // Ensure the parent product exists
        itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        ItemVariant itemVariant = mapper.toEntity(dto);
        itemVariantRepository.save(itemVariant);
        return mapper.toDto(itemVariant);
    }

    @Transactional
    public ItemVariantDto update(Long id, ItemVariantDto dto) {
        ItemVariant itemVariant = itemVariantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item Variant not found"));

        itemVariant.setSku(dto.getSku());
        itemVariant.setPricePerUnit(dto.getPricePerUnit());
        itemVariant.setGstRate(dto.getGstRate());
        itemVariant.setHsn(dto.getHsn());
        // etc. update other fields

        itemVariantRepository.save(itemVariant);
        return mapper.toDto(itemVariant);
    }

    public List<ItemVariantDto> list(Pageable pageable) {
        Page<ItemVariant> variants = itemVariantRepository.findAll(pageable);
        return variants.stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public ItemVariantDto get(Long id) {
        return itemVariantRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new RuntimeException("Item Variant not found"));
    }

    public List<ItemVariantDto> searchItemVariants(String name, String category, String color, String size,String style,String sku){
        List<ItemVariant> variants = itemVariantRepository.searchVariants(name, category, color, size, style,sku);
        List<ItemVariantDto> itemVariantDtos = new ArrayList<>();
        for(ItemVariant itemVariant : variants){
            ItemVariantDto dto = mapper.toDto(itemVariant);
            dto.setItemName(itemVariant.getItem().getName());
            dto.setCategory(itemVariant.getItem().getCategory());
            dto.setCurrentStock(stockService.getCurrentStock(itemVariant.getId()));
            itemVariantDtos.add(dto);
        }
        /*return variants.stream().map(variant -> {
            ItemVariantDto dto = mapper.toDto(variant);
            dto.setItemName(variant.getItem().getName());
            dto.setCategory(variant.getItem().getCategory());
            dto.setCurrentStock(stockService.getCurrentStock(variant.getId()));
            return dto;
        }).collect(Collectors.toList());*/
        return itemVariantDtos;
    }
}