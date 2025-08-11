package com.desitech.vyaparsathi.customer.controller;

import com.desitech.vyaparsathi.customer.dto.CustomerDto;
import com.desitech.vyaparsathi.customer.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
@PreAuthorize("hasAnyRole('OWNER', 'STAFF')")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerDto> addCustomer(@RequestBody CustomerDto dto) {
        return ResponseEntity.ok(customerService.addCustomer(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDto> updateCustomer(@PathVariable Long id, @RequestBody CustomerDto dto) {
        return ResponseEntity.ok(customerService.updateCustomer(id, dto));
    }

    @GetMapping
    public ResponseEntity<List<CustomerDto>> listCustomers(@RequestParam(required = false) String name) {
        if (name != null && !name.isEmpty()) {
            return ResponseEntity.ok(customerService.searchCustomers(name));
        }
        return ResponseEntity.ok(customerService.listCustomers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDto> getCustomer(@PathVariable Long id) {
        return customerService.getCustomer(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}