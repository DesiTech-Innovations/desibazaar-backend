package com.desitech.vyaparsathi.sales.controller;

import com.desitech.vyaparsathi.sales.dto.SaleDto;
import com.desitech.vyaparsathi.sales.service.SaleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
@PreAuthorize("hasAnyRole('OWNER', 'STAFF')")
public class SaleController {
    @Autowired
    private SaleService service;

    @PostMapping
    public ResponseEntity<byte[]> create(@Valid @RequestBody SaleDto dto) {
        byte[] pdf = service.createSale(dto);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice.pdf")
                .body(pdf);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleDto> getSale(@PathVariable Long id) {
        return service.getSaleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<SaleDto>> listSales(
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {
        return ResponseEntity.ok(service.listSales(startDate, endDate));
    }

}