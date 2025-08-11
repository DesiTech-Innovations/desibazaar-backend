package com.desitech.vyaparsathi.inventory.repository;

import com.desitech.vyaparsathi.inventory.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Integer> {
}
