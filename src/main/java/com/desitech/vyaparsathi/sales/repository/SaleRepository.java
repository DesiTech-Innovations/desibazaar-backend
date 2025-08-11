package com.desitech.vyaparsathi.sales.repository;

import com.desitech.vyaparsathi.sales.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {
    @Query("SELECT s FROM Sale s WHERE (:startDate IS NULL OR s.date >= :startDate) AND (:endDate IS NULL OR s.date <= :endDate) ORDER BY s.date DESC")
    List<Sale> findByDateBetween(@Param("startDate") LocalDateTime start, @Param("endDate") LocalDateTime end);
}