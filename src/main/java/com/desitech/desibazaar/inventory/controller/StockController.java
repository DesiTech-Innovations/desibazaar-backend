package com.desitech.desibazaar.inventory.controller;

import com.desitech.desibazaar.inventory.dto.CurrentStockDto;
import com.desitech.desibazaar.inventory.dto.StockAddDto;
import com.desitech.desibazaar.inventory.dto.StockEntryDto;
import com.desitech.desibazaar.inventory.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
@PreAuthorize("hasAnyRole('OWNER', 'STAFF')")
public class StockController {

    @Autowired
    private StockService service;

    @PostMapping("/add")
    public ResponseEntity<StockEntryDto> addStock(@RequestBody StockAddDto dto) {
        return ResponseEntity.ok(service.addStock(dto));
    }

    @GetMapping
    public ResponseEntity<List<CurrentStockDto>> getCurrentStock() {
        return ResponseEntity.ok(service.getCurrentStock());
    }
}