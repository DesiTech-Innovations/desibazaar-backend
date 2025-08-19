package com.desitech.vyaparsathi.inventory.repository;

import com.desitech.vyaparsathi.inventory.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    List<StockMovement> findByItemVariantIdOrderByTimestampDesc(Long itemVariantId);
    List<StockMovement> findByItemVariantIdAndTimestampBetweenOrderByTimestampDesc(
        Long itemVariantId, LocalDateTime startDate, LocalDateTime endDate);
    List<StockMovement> findByTimestampBetweenOrderByTimestampDesc(
        LocalDateTime startDate, LocalDateTime endDate);
}