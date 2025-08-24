package com.desitech.vyaparsathi.sales.controller;

import com.desitech.vyaparsathi.common.exception.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger logger = LoggerFactory.getLogger(SaleController.class);
    @Autowired
    private SaleService service;

    @PostMapping
    public ResponseEntity<byte[]> create(@Valid @RequestBody SaleDto dto) {
        try {
            byte[] pdf = service.createSale(dto);
            logger.info("Created sale and generated invoice for customerId={}", dto.getCustomerId());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice.pdf")
                    .body(pdf);
        } catch (Exception e) {
            logger.error("Error creating sale for customerId={}: {}", dto.getCustomerId(), e.getMessage(), e);
            throw new ApplicationException("Failed to create sale", e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleDto> getSale(@PathVariable Long id) {
        try {
            var result = service.getSaleById(id);
            if (result.isPresent()) {
                logger.info("Fetched sale with id={}", id);
                return ResponseEntity.ok(result.get());
            } else {
                logger.warn("Sale not found with id={}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error fetching sale with id={}: {}", id, e.getMessage(), e);
            throw new ApplicationException("Failed to fetch sale", e);
        }
    }

    @GetMapping
    public ResponseEntity<List<SaleDto>> listSales(
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {
        try {
            var result = service.listSales(startDate, endDate);
            logger.info("Fetched sales list from {} to {}", startDate, endDate);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error fetching sales list from {} to {}: {}", startDate, endDate, e.getMessage(), e);
            throw new ApplicationException("Failed to fetch sales list", e);
        }
    }

    @GetMapping("/with-due")
    public ResponseEntity<List<SaleDueDto>> getSalesWithDue() {
        try {
            var result = service.getSalesWithDue();
            logger.info("Fetched sales with due");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error fetching sales with due: {}", e.getMessage(), e);
            throw new ApplicationException("Failed to fetch sales with due", e);
        }
    }

    @GetMapping("/{id}/due")
    public ResponseEntity<SaleDueDto> getSaleDueBySaleId(@PathVariable Long id) {
        try {
            var result = service.getSaleDueBySaleId(id);
            logger.info("Fetched sale due for saleId={}", id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error fetching sale due for saleId={}: {}", id, e.getMessage(), e);
            throw new ApplicationException("Failed to fetch sale due", e);
        }
    }
    @GetMapping("/{customerId}/dues")
    public ResponseEntity<Page<SaleDueDto>> getCustomerDues(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<SaleDueDto> dues = service.getDuesByCustomerId(customerId, pageable);
            logger.info("Fetched customer dues for customerId={}", customerId);
            return ResponseEntity.ok(dues);
        } catch (Exception e) {
            logger.error("Error fetching customer dues for customerId={}: {}", customerId, e.getMessage(), e);
            throw new ApplicationException("Failed to fetch customer dues", e);
        }
    }

    @PostMapping("/{id}/return")
    @Operation(summary = "Process sale return", 
               description = "Process partial or full return of items from a sale. Automatically restores stock and handles payment/ledger reversal if requested.")
    @ApiResponse(responseCode = "200", description = "Sale return processed successfully")
    public ResponseEntity<Void> processSaleReturn(
            @Parameter(description = "Sale ID") @PathVariable Long id, 
            @RequestBody SaleReturnDto returnDto) {
        try {
            returnDto.setSaleId(id); // Ensure consistency
            service.processSaleReturn(returnDto);
            logger.info("Processed sale return for saleId={}", id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error processing sale return for saleId={}: {}", id, e.getMessage(), e);
            throw new ApplicationException("Failed to process sale return", e);
        }
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel entire sale", 
               description = "Cancel an entire sale transaction. Restores all stock, reverses all payments and ledger entries. Irreversible action.")
    @ApiResponse(responseCode = "200", description = "Sale cancelled successfully")
    public ResponseEntity<Void> cancelSale(
            @Parameter(description = "Sale ID") @PathVariable Long id, 
            @Parameter(description = "Reason for cancellation") @RequestParam String reason) {
        try {
            service.cancelSale(id, reason);
            logger.info("Cancelled sale with id={}", id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error cancelling sale with id={}: {}", id, e.getMessage(), e);
            throw new ApplicationException("Failed to cancel sale", e);
        }
    }

    @GetMapping("/profit-report")
    @Operation(summary = "Get sales profit report", 
               description = "Generate profit analysis report showing revenue, COGS, gross profit and margin percentage for sales within date range")
    @ApiResponse(responseCode = "200", description = "Profit report generated successfully")
    public ResponseEntity<List<SalesProfitDto>> getSalesProfitReport(
            @Parameter(description = "Start date for profit report") @RequestParam LocalDateTime startDate,
            @Parameter(description = "End date for profit report") @RequestParam LocalDateTime endDate) {
        try {
            var result = service.getSalesProfitReport(startDate, endDate);
            logger.info("Fetched sales profit report from {} to {}", startDate, endDate);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error fetching sales profit report from {} to {}: {}", startDate, endDate, e.getMessage(), e);
            throw new ApplicationException("Failed to fetch sales profit report", e);
        }
    }

}