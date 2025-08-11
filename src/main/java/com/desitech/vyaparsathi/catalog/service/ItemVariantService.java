package com.desitech.vyaparsathi.catalog.service;

import com.desitech.vyaparsathi.catalog.dto.ItemVariantDto;
import com.desitech.vyaparsathi.catalog.entity.ItemVariant;
import com.desitech.vyaparsathi.catalog.mapper.CatalogMapper;
import com.desitech.vyaparsathi.catalog.repository.ItemVariantRepository;
import com.desitech.vyaparsathi.catalog.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemVariantService {

    @Autowired
    private ItemVariantRepository itemVariantRepository;
    @Autowired
    private ItemRepository productRepository;
    @Autowired
    private CatalogMapper mapper;

    @Transactional
    public ItemVariantDto create(ItemVariantDto dto) {
        // Ensure the parent product exists
        productRepository.findById(dto.getProductId())
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
}