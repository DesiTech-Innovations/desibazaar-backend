package com.desitech.vyaparsathi.catalog.service;

import com.desitech.vyaparsathi.catalog.dto.ItemDto;
import com.desitech.vyaparsathi.catalog.entity.Item;
import com.desitech.vyaparsathi.catalog.mapper.CatalogMapper;
import com.desitech.vyaparsathi.catalog.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CatalogMapper mapper;

    @Transactional
    public ItemDto create(ItemDto dto) {
        Item item = mapper.toEntity(dto);
        itemRepository.save(item);
        return mapper.toDto(item);
    }

    @Transactional
    public ItemDto update(Long id, ItemDto dto) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        // Handle variants update here if needed
        itemRepository.save(item);
        return mapper.toDto(item);
    }

    public List<ItemDto> list(Pageable pageable) {
        Page<Item> products = itemRepository.findAll(pageable);
        return products.stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public ItemDto get(Long id) {
        return itemRepository.findById(id).map(mapper::toDto).orElseThrow(() -> new RuntimeException("Product not found"));
    }
}
