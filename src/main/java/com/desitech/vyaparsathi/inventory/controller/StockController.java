package com.desitech.vyaparsathi.inventory.controller;

import com.desitech.vyaparsathi.inventory.dto.*;
import com.desitech.vyaparsathi.inventory.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/stock")
@PreAuthorize("hasAnyRole('OWNER', 'STAFF')")
public class StockController {

    @Autowired
    private StockService service;

    @PostMapping("/add")
    public ResponseEntity<StockEntryDto> addStock(@RequestBody StockAddDto dto) {
        return ResponseEntity.ok(service.addStockFromDto(dto));
    }

    @GetMapping
    public ResponseEntity<List<CurrentStockDto>> getCurrentStock() {
        return ResponseEntity.ok(service.getCurrentStock());
    }

    @GetMapping("/movements/{itemVariantId}")
    public ResponseEntity<List<StockMovementDto>> getStockMovements(@PathVariable Long itemVariantId) {
        return ResponseEntity.ok(service.getStockMovements(itemVariantId));
    }

    @GetMapping("/movements")
    public ResponseEntity<List<StockMovementDto>> getStockMovements(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(service.getStockMovements(startDate, endDate));
    }

    @GetMapping("/low-stock-alerts")
    public ResponseEntity<List<LowStockAlertDto>> getLowStockAlerts() {
        return ResponseEntity.ok(service.getLowStockAlerts());
    }

    @PostMapping("/adjust")
    public ResponseEntity<StockEntryDto> adjustStock(@RequestBody StockAdjustmentDto dto) {
        StockEntryDto result = service.adjustStock(dto);
        return ResponseEntity.ok(result);
    }
}