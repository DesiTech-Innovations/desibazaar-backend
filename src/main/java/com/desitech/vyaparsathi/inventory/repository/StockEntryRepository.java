package com.desitech.vyaparsathi.inventory.repository;

import com.desitech.vyaparsathi.inventory.entity.StockEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface StockEntryRepository extends JpaRepository<StockEntry, Long> {

    // Update query to find by itemVariant
    List<StockEntry> findByItemVariantId(Long itemVariantId);

    // Update query to sum quantity by itemVariantId
    @Query("SELECT SUM(se.quantity) FROM StockEntry se WHERE se.itemVariant.id = :itemVariantId")
    BigDecimal getTotalQuantityByItemVariantId(@Param("itemVariantId") Long itemVariantId);

    List<StockEntry> findByItemVariantIdOrderByLastUpdatedDesc(Long itemVariantId);
}