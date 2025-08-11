package com.desitech.vyaparsathi.inventory.repository;

import com.desitech.vyaparsathi.inventory.entity.PurchaseOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, Integer> {
}
