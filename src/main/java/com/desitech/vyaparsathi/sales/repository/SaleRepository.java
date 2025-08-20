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

public interface SaleRepository extends JpaRepository<Sale, Long> {
    @Query("SELECT s FROM Sale s JOIN FETCH s.customer JOIN FETCH s.payments WHERE s.customer.id = :customerId")
    Page<Sale> findByCustomerId(@Param("customerId") Long customerId, Pageable pageable);

    @EntityGraph(attributePaths = {"saleItems", "customer", "payments"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("SELECT s FROM Sale s WHERE (:startDate IS NULL OR s.date >= :startDate) AND (:endDate IS NULL OR s.date <= :endDate) ORDER BY s.date DESC")
    List<Sale> findByDateBetween(@Param("startDate") LocalDateTime start, @Param("endDate") LocalDateTime end);

    @EntityGraph(attributePaths = {"saleItems", "customer", "payments"}, type = EntityGraph.EntityGraphType.LOAD)
    List<Sale> findAll();

    // Optional: Keep this if needed for other use cases
    /*
    @Query("SELECT s FROM Sale s JOIN FETCH s.payments")
    List<Sale> findAllWithPayments();
    */
}