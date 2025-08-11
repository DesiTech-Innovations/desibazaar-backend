package com.desitech.vyaparsathi.expense.controller;

import com.desitech.vyaparsathi.expense.dto.ExpenseDto;
import com.desitech.vyaparsathi.expense.dto.UpdateExpenseDto;
import com.desitech.vyaparsathi.expense.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/expenses")
@PreAuthorize("hasAnyRole('OWNER', 'STAFF')")
public class ExpenseController {

    @Autowired
    private ExpenseService service;

    @PostMapping
    public ResponseEntity<ExpenseDto> create(@Valid @RequestBody ExpenseDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @GetMapping
    public ResponseEntity<Page<ExpenseDto>> list(Pageable pageable, @RequestParam(required = false, defaultValue = "1") Long shopId) {
        return ResponseEntity.ok(service.list(pageable, shopId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseDto> update(@PathVariable Long id, @Valid @RequestBody UpdateExpenseDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}