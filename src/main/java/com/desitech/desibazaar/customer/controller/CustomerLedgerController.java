package com.desitech.desibazaar.customer.controller;

import com.desitech.desibazaar.customer.entity.CustomerLedger;
import com.desitech.desibazaar.customer.service.CustomerLedgerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers/{customerId}/ledger")
public class CustomerLedgerController {
    private final CustomerLedgerService ledgerService;

    public CustomerLedgerController(CustomerLedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    @PostMapping
    public ResponseEntity<CustomerLedger> addLedgerEntry(
            @PathVariable Long customerId,
            @RequestParam Double amount,
            @RequestParam String type, // CREDIT or DEBIT
            @RequestParam(required = false) String description) {

        return ResponseEntity.ok(
                ledgerService.addEntry(customerId, amount, type, description)
        );
    }

    @GetMapping
    public ResponseEntity<List<CustomerLedger>> getLedger(@PathVariable Long customerId) {
        return ResponseEntity.ok(ledgerService.getLedger(customerId));
    }
}
