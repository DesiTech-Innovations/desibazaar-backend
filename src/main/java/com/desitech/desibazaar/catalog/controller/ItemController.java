package com.desitech.desibazaar.catalog.controller;

import com.desitech.desibazaar.catalog.dto.ItemDto;
import com.desitech.desibazaar.catalog.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@PreAuthorize("hasAnyRole('OWNER', 'STAFF')")
public class ItemController {
    @Autowired
    private ItemService service;

    @PostMapping
    public ResponseEntity<ItemDto> create(@RequestBody ItemDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemDto> update(@PathVariable Long id, @RequestBody ItemDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> list(Pageable pageable) {
        return ResponseEntity.ok(service.list(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }
}