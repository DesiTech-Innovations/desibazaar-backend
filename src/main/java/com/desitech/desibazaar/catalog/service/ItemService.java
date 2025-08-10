package com.desitech.desibazaar.catalog.service;

import com.desitech.desibazaar.catalog.dto.ItemDto;
import com.desitech.desibazaar.catalog.entity.Item;
import com.desitech.desibazaar.catalog.mapper.ItemMapper;
import com.desitech.desibazaar.catalog.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemService {
    @Autowired
    private ItemRepository repository;
    @Autowired
    private ItemMapper mapper;

    public ItemDto create(ItemDto dto) {
        Item item = mapper.toEntity(dto);
        repository.save(item);
        return mapper.toDto(item);
    }

    public ItemDto update(Long id, ItemDto dto) {
        Item item = repository.findById(id).orElseThrow();
        // Update fields (use mapper or manual)
        item.setName(dto.getName());
        item.setPricePerUnit(dto.getPricePerUnit());
        // etc.
        repository.save(item);
        return mapper.toDto(item);
    }

    public List<ItemDto> list(Pageable pageable) {
        return repository.findAll(pageable).stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public ItemDto get(Long id) {
        return repository.findById(id).map(mapper::toDto).orElseThrow();
    }
}