package com.desitech.vyaparsathi.purchaseorder.repository;

import com.desitech.vyaparsathi.purchaseorder.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
}
