package com.desitech.vyaparsathi.sales.controller;

import com.desitech.vyaparsathi.sales.dto.SaleDto;
import com.desitech.vyaparsathi.sales.dto.SaleDueDto;
import com.desitech.vyaparsathi.sales.dto.SaleReturnDto;
import com.desitech.vyaparsathi.sales.dto.SalesProfitDto;
import com.desitech.vyaparsathi.sales.service.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
@Tag(name = "Sales Management", description = "Operations for sales management including COGS tracking, returns, cancellations, and profit reporting")
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

    @GetMapping("/with-due")
    public ResponseEntity<List<SaleDueDto>> getSalesWithDue() {
        return ResponseEntity.ok(service.getSalesWithDue());
    }

    @GetMapping("/{id}/due")
    public ResponseEntity<SaleDueDto> getSaleDueBySaleId(@PathVariable Long id) {
        return ResponseEntity.ok(service.getSaleDueBySaleId(id));
    }
    @GetMapping("/{customerId}/dues")
    public ResponseEntity<Page<SaleDueDto>> getCustomerDues(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SaleDueDto> dues = service.getDuesByCustomerId(customerId, pageable);
        return ResponseEntity.ok(dues);
    }

    @PostMapping("/{id}/return")
    @Operation(summary = "Process sale return", 
               description = "Process partial or full return of items from a sale. Automatically restores stock and handles payment/ledger reversal if requested.")
    @ApiResponse(responseCode = "200", description = "Sale return processed successfully")
    public ResponseEntity<Void> processSaleReturn(
            @Parameter(description = "Sale ID") @PathVariable Long id, 
            @RequestBody SaleReturnDto returnDto) {
        returnDto.setSaleId(id); // Ensure consistency
        service.processSaleReturn(returnDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel entire sale", 
               description = "Cancel an entire sale transaction. Restores all stock, reverses all payments and ledger entries. Irreversible action.")
    @ApiResponse(responseCode = "200", description = "Sale cancelled successfully")
    public ResponseEntity<Void> cancelSale(
            @Parameter(description = "Sale ID") @PathVariable Long id, 
            @Parameter(description = "Reason for cancellation") @RequestParam String reason) {
        service.cancelSale(id, reason);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/profit-report")
    @Operation(summary = "Get sales profit report", 
               description = "Generate profit analysis report showing revenue, COGS, gross profit and margin percentage for sales within date range")
    @ApiResponse(responseCode = "200", description = "Profit report generated successfully")
    public ResponseEntity<List<SalesProfitDto>> getSalesProfitReport(
            @Parameter(description = "Start date for profit report") @RequestParam LocalDateTime startDate,
            @Parameter(description = "End date for profit report") @RequestParam LocalDateTime endDate) {
        return ResponseEntity.ok(service.getSalesProfitReport(startDate, endDate));
    }

}