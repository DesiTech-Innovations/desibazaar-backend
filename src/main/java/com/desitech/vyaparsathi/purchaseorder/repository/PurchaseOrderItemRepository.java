package com.desitech.vyaparsathi.purchaseorder.repository;

import com.desitech.vyaparsathi.purchaseorder.entity.PurchaseOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, Integer> {
    @Query("SELECT poi FROM PurchaseOrderItem poi WHERE poi.itemVariant.id = :itemVariantId")
    List<PurchaseOrderItem> findByItemVariantId(@Param("itemVariantId") Long itemVariantId);
}
