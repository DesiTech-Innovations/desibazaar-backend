package com.desitech.desibazaar.inventory.repository;

import com.desitech.desibazaar.inventory.entity.StockEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface StockEntryRepository extends JpaRepository<StockEntry, Long> {

    List<StockEntry> findByItemId(Long itemId);

    @Query("SELECT SUM(se.quantity) FROM StockEntry se WHERE se.itemId = :itemId")
    BigDecimal getTotalQuantityByItemId(@Param("itemId") Long itemId);
}