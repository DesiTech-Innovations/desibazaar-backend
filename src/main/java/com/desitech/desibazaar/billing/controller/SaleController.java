package com.desitech.desibazaar.billing.controller;

import com.desitech.desibazaar.billing.dto.SaleDto;
import com.desitech.desibazaar.billing.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sales")
@PreAuthorize("hasAnyRole('OWNER', 'STAFF')")
public class SaleController {
    @Autowired
    private SaleService service;

    @PostMapping
    public ResponseEntity<byte[]> create(@RequestBody SaleDto dto) {
        byte[] pdf = service.createSale(dto);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice.pdf")
                .body(pdf);
    }

    // Add GET endpoints later
}