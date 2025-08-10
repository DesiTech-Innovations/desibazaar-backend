package com.desitech.desibazaar.expense.service;

import com.desitech.desibazaar.changelog.service.ChangeLogService;
import com.desitech.desibazaar.expense.dto.ExpenseDto;
import com.desitech.desibazaar.expense.entity.Expense;
import com.desitech.desibazaar.expense.mapper.ExpenseMapper;
import com.desitech.desibazaar.expense.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository repository;

    @Autowired
    private ExpenseMapper mapper;

    @Autowired
    private ChangeLogService changeLogService;  // For auditing

    @Transactional
    public ExpenseDto create(ExpenseDto dto) {
        Expense expense = mapper.toEntity(dto);
        repository.save(expense);
        changeLogService.append("EXPENSE", expense.getId(), "CREATE", dto, "LOCAL_DEVICE");
        return mapper.toDto(expense);
    }

    public List<ExpenseDto> list(Pageable pageable, Long shopId) {
        Page<Expense> expenses = repository.findByShopId(shopId, pageable);
        return expenses.stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public ExpenseDto get(Long id) {
        Expense expense = repository.findById(id)
                .filter(e -> !e.isDeleted())
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        return mapper.toDto(expense);
    }

    @Transactional
    public ExpenseDto update(Long id, ExpenseDto dto) {
        Expense expense = repository.findById(id)
                .filter(e -> !e.isDeleted())
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        // Update fields
        expense.setType(dto.getType());
        expense.setAmount(dto.getAmount());
        expense.setDate(dto.getDate());
        expense.setNotes(dto.getNotes());
        repository.save(expense);
        changeLogService.append("EXPENSE", expense.getId(), "UPDATE", dto, "LOCAL_DEVICE");
        return mapper.toDto(expense);
    }

    @Transactional
    public void delete(Long id) {
        Expense expense = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        expense.setDeleted(true);
        repository.save(expense);
        changeLogService.append("EXPENSE", id, "DELETE", null, "LOCAL_DEVICE");
    }
}