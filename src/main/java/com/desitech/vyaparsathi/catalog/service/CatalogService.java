package com.desitech.vyaparsathi.catalog.service;

import com.desitech.vyaparsathi.catalog.dto.ItemDto;
import com.desitech.vyaparsathi.catalog.dto.ItemVariantDto;
import com.desitech.vyaparsathi.catalog.entity.Item;
import com.desitech.vyaparsathi.catalog.entity.ItemVariant;
import com.desitech.vyaparsathi.catalog.mapper.CatalogMapper;
import com.desitech.vyaparsathi.catalog.repository.ItemRepository;
import com.desitech.vyaparsathi.catalog.repository.ItemVariantRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CatalogService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemVariantRepository itemVariantRepository;

    @Autowired
    private CatalogMapper mapper;

    // --- Product (Item) Management ---

    public List<ItemDto> getAllItems() {
        return itemRepository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public ItemDto getItemById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item not found with id: " + id));
        return mapper.toDto(item);
    }

    @Transactional
    public ItemDto createItem(ItemDto itemDto) {
        // Validation for item name uniqueness could be added here
        Item item = mapper.toEntity(itemDto);
        item = itemRepository.save(item);
        return mapper.toDto(item);
    }

    public List<ItemDto> createItems(List<ItemDto> items) {
        // Map DTOs to entities
        List<Item> entities = items.stream()
                .map(mapper::toEntity)
                .collect(Collectors.toList());

        // Save all at once
        List<Item> savedEntities = itemRepository.saveAll(entities);

        // Map back to DTOs
        return savedEntities.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }


    @Transactional
    public ItemDto updateItem(Long id, ItemDto itemDto) {
        Item existingItem = itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item not found with id: " + id));

        // Update top-level item properties
        existingItem.setName(itemDto.getName());
        existingItem.setDescription(itemDto.getDescription());

        // Handle variants within the item update
        List<ItemVariant> updatedVariants = itemDto.getVariants().stream()
                .map(variantDto -> {
                    ItemVariant variant = mapper.toEntity(variantDto);
                    variant.setItem(existingItem);
                    return variant;
                })
                .collect(Collectors.toList());

        existingItem.getVariants().clear();
        existingItem.getVariants().addAll(updatedVariants);

        itemRepository.save(existingItem);
        return mapper.toDto(existingItem);
    }

    @Transactional
    public void deleteItem(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new EntityNotFoundException("Item not found with id: " + id);
        }
        itemRepository.deleteById(id);
    }

    // --- Item Variant Management ---

    public List<ItemVariantDto> getAllItemVariants() {
        return itemVariantRepository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public ItemVariantDto getItemVariantById(Long id) {
        ItemVariant variant = itemVariantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item Variant not found with id: " + id));
        return mapper.toDto(variant);
    }

    @Transactional
    public ItemVariantDto createItemVariant(Long itemId, ItemVariantDto variantDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found with id: " + itemId));
        if (itemVariantRepository.findBySku(variantDto.getSku()).isPresent()) {
            throw new IllegalArgumentException("SKU already exists: " + variantDto.getSku());
        }

        ItemVariant variant = mapper.toEntity(variantDto);
        variant.setItem(item);
        itemVariantRepository.save(variant);
        return mapper.toDto(variant);
    }
}
