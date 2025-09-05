package com.desitech.vyaparsathi.sales.repository;

import com.desitech.vyaparsathi.sales.entity.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SaleRepository extends JpaRepository<Sale, Long> {
    // Removed JOIN FETCH s.payments
    @Query("SELECT s FROM Sale s JOIN FETCH s.customer WHERE s.customer.id = :customerId")
    Page<Sale> findByCustomerId(@Param("customerId") Long customerId, Pageable pageable);

    @EntityGraph(attributePaths = {"saleItems", "customer"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT s FROM Sale s WHERE (:startDate IS NULL OR s.date >= :startDate) AND (:endDate IS NULL OR s.date <= :endDate) ORDER BY s.date DESC")
    List<Sale> findByDateBetween(@Param("startDate") LocalDateTime start, @Param("endDate") LocalDateTime end);

    @EntityGraph(attributePaths = {"saleItems", "customer"}, type = EntityGraph.EntityGraphType.LOAD)
    List<Sale> findAll();

    @EntityGraph(attributePaths = {"saleItems", "customer"}, type = EntityGraph.EntityGraphType.LOAD)
    Sale findByInvoiceNo(String invoiceNo);

    /**
     * NEW: Finds the most recent sale for a given customer.
     * This is used by the AnalyticsService to predict customer churn.
     */
    Optional<Sale> findTopByCustomerIdOrderByDateDesc(Long customerId);
}