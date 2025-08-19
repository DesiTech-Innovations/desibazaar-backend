package com.desitech.vyaparsathi.inventory.controller;

import com.desitech.vyaparsathi.inventory.dto.*;
import com.desitech.vyaparsathi.inventory.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Stock Management", description = "Operations for inventory stock management including cost tracking, movement history, and stock adjustments")
public class StockController {

    @Autowired
    private StockService service;

    @PostMapping("/add")
    @Operation(summary = "Add stock manually", 
               description = "Manually add stock with cost tracking. Records stock movement for audit trail.")
    @ApiResponse(responseCode = "200", description = "Stock added successfully")
    public ResponseEntity<StockEntryDto> addStock(@RequestBody StockAddDto dto) {
        return ResponseEntity.ok(service.addStockFromDto(dto));
    }

    @GetMapping
    @Operation(summary = "Get current stock levels", 
               description = "Retrieve current stock levels for all item variants")
    @ApiResponse(responseCode = "200", description = "Current stock levels retrieved successfully")
    public ResponseEntity<List<CurrentStockDto>> getCurrentStock() {
        return ResponseEntity.ok(service.getCurrentStock());
    }

    @GetMapping("/movements/{itemVariantId}")
    @Operation(summary = "Get stock movement history for item", 
               description = "Retrieve complete stock movement history for a specific item variant")
    @ApiResponse(responseCode = "200", description = "Stock movements retrieved successfully")
    public ResponseEntity<List<StockMovementDto>> getStockMovements(
            @Parameter(description = "ID of the item variant") @PathVariable Long itemVariantId) {
        return ResponseEntity.ok(service.getStockMovements(itemVariantId));
    }

    @GetMapping("/movements")
    @Operation(summary = "Get stock movements within date range", 
               description = "Retrieve all stock movements within specified date range for reporting purposes")
    @ApiResponse(responseCode = "200", description = "Stock movements retrieved successfully")
    public ResponseEntity<List<StockMovementDto>> getStockMovements(
            @Parameter(description = "Start date for the report") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date for the report") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(service.getStockMovements(startDate, endDate));
    }

    @GetMapping("/low-stock-alerts")
    @Operation(summary = "Get low stock alerts", 
               description = "Retrieve items that are below their configured stock thresholds")
    @ApiResponse(responseCode = "200", description = "Low stock alerts retrieved successfully")
    public ResponseEntity<List<LowStockAlertDto>> getLowStockAlerts() {
        return ResponseEntity.ok(service.getLowStockAlerts());
    }

    @PostMapping("/adjust")
    @Operation(summary = "Manual stock adjustment", 
               description = "Perform manual stock adjustment (positive to add, negative to reduce). Reason is mandatory for audit purposes.")
    @ApiResponse(responseCode = "200", description = "Stock adjusted successfully")
    public ResponseEntity<StockEntryDto> adjustStock(@RequestBody StockAdjustmentDto dto) {
        StockEntryDto result = service.adjustStock(dto);
        return ResponseEntity.ok(result);
    }
}