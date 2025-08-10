package com.desitech.desibazaar.expense.controller;

import com.desitech.desibazaar.expense.dto.ExpenseDto;
import com.desitech.desibazaar.expense.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@PreAuthorize("hasAnyRole('OWNER', 'STAFF')")
public class ExpenseController {

    @Autowired
    private ExpenseService service;

    @PostMapping
    public ResponseEntity<ExpenseDto> create(@RequestBody ExpenseDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<ExpenseDto>> list(Pageable pageable, @RequestParam(required = false, defaultValue = "1") Long shopId) {
        return ResponseEntity.ok(service.list(pageable, shopId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseDto> update(@PathVariable Long id, @RequestBody ExpenseDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}