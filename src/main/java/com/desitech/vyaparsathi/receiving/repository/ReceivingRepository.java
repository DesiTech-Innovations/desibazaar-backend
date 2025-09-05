package com.desitech.vyaparsathi.receiving.repository;

import com.desitech.vyaparsathi.receiving.entity.Receiving;
import com.desitech.vyaparsathi.purchaseorder.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReceivingRepository extends JpaRepository<Receiving, Long> {
    boolean existsByPurchaseOrder(PurchaseOrder po);
    Optional<Receiving> findByPurchaseOrderId(Long poId);
}