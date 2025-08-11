package com.desitech.vyaparsathi.catalog.controller;

import com.desitech.vyaparsathi.catalog.dto.ItemVariantDto;
import com.desitech.vyaparsathi.catalog.service.ItemVariantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/item-variants")
@PreAuthorize("hasAnyRole('OWNER', 'STAFF')")
public class ItemVariantController {

    @Autowired
    private ItemVariantService service;

    @PostMapping
    public ResponseEntity<ItemVariantDto> create(@RequestBody ItemVariantDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemVariantDto> update(@PathVariable Long id, @RequestBody ItemVariantDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @GetMapping
    public ResponseEntity<List<ItemVariantDto>> list(Pageable pageable) {
        return ResponseEntity.ok(service.list(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemVariantDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }
}