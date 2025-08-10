package com.desitech.desibazaar.expense.repository;

import com.desitech.desibazaar.expense.entity.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("SELECT e FROM Expense e WHERE e.deleted = false AND e.shopId = :shopId")
    Page<Expense> findByShopId(@Param("shopId") Long shopId, Pageable pageable);

    // Optional: Filter by date range for reports
    List<Expense> findByDateBetweenAndDeletedFalse(LocalDateTime start, LocalDateTime end);
}