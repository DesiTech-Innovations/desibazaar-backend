package com.desitech.vyaparsathi.inventory.controller;

import com.desitech.vyaparsathi.inventory.dto.SupplierDto;
import com.desitech.vyaparsathi.inventory.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@PreAuthorize("hasRole('OWNER')")
public class SupplierController {

    @Autowired
    private SupplierService service;

    @PostMapping
    public ResponseEntity<SupplierDto> createSupplier(@RequestBody SupplierDto dto) {
        return ResponseEntity.ok(service.createSupplier(dto));
    }

    @GetMapping
    public ResponseEntity<List<SupplierDto>> getAllSuppliers() {
        return ResponseEntity.ok(service.findAllSuppliers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierDto> getSupplierById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.findSupplierById(id));
    }
}
