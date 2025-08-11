package com.desitech.vyaparsathi.customer.controller;

import com.desitech.vyaparsathi.customer.dto.CustomerLedgerDto;
import com.desitech.vyaparsathi.customer.service.CustomerLedgerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/customers/{customerId}/ledger")
@PreAuthorize("hasAnyRole('OWNER', 'STAFF')")
public class CustomerLedgerController {
    @Autowired
    private CustomerLedgerService ledgerService;

    @PostMapping
    public ResponseEntity<CustomerLedgerDto> addLedgerEntry(
            @PathVariable Long customerId,
            @Valid @RequestBody CustomerLedgerDto dto) {
        return ResponseEntity.ok(ledgerService.addEntry(customerId, dto));
    }

    @PutMapping("/{ledgerId}")
    public ResponseEntity<CustomerLedgerDto> updateLedgerEntry(
            @PathVariable Long ledgerId,
            @Valid @RequestBody CustomerLedgerDto dto) {
        return ResponseEntity.ok(ledgerService.updateEntry(ledgerId, dto));
    }

    @GetMapping
    public ResponseEntity<List<CustomerLedgerDto>> getLedger(
            @PathVariable Long customerId,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {
        return ResponseEntity.ok(ledgerService.getLedger(customerId, startDate, endDate));
    }

    @DeleteMapping("/{ledgerId}")
    public ResponseEntity<Void> deleteLedgerEntry(@PathVariable Long ledgerId) {
        ledgerService.deleteEntry(ledgerId);
        return ResponseEntity.noContent().build();
    }
}