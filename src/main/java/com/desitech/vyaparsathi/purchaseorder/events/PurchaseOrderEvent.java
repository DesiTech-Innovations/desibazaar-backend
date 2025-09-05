package com.desitech.vyaparsathi.purchaseorder.events;

import com.desitech.vyaparsathi.purchaseorder.entity.PurchaseOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseOrderEvent {
    private String eventType; // CREATED, PLACED, APPROVED, UPDATED, DELETED
    private PurchaseOrder purchaseOrder;
}