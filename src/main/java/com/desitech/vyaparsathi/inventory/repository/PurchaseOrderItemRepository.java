package com.desitech.vyaparsathi.inventory.repository;

import com.desitech.vyaparsathi.inventory.entity.PurchaseOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, Integer> {
    
    /**
     * Find all purchase order items for a specific item variant
     * Used for COGS calculation to get purchase costs
     */
    @Query("SELECT poi FROM PurchaseOrderItem poi WHERE poi.itemVariant.id = :itemVariantId")
    List<PurchaseOrderItem> findByItemVariantId(@Param("itemVariantId") Long itemVariantId);
}
