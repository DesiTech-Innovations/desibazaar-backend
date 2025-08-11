package com.desitech.vyaparsathi.catalog.controller;

import com.desitech.vyaparsathi.catalog.dto.ItemDto;
import com.desitech.vyaparsathi.catalog.service.CatalogService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    @Autowired
    private CatalogService service;

    @GetMapping
    public List<ItemDto> getAllItems() {
        return service.getAllItems();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getItemById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<ItemDto> createItem(@Valid @RequestBody ItemDto itemDto) {
        return ResponseEntity.ok(service.createItem(itemDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<ItemDto> updateItem(@PathVariable Long id, @Valid @RequestBody ItemDto itemDto) {
        return ResponseEntity.ok(service.updateItem(id, itemDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        service.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}
