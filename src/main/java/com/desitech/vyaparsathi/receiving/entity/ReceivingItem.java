package com.desitech.vyaparsathi.receiving.entity;

import com.desitech.vyaparsathi.purchaseorder.entity.PurchaseOrderItem;
import com.desitech.vyaparsathi.receiving.enums.ReceivingItemStatus;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class ReceivingItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiving_id", nullable = false)
    private Receiving receiving;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "po_item_id", nullable = false)
    private PurchaseOrderItem purchaseOrderItem;

    @Enumerated(EnumType.STRING)
    private ReceivingItemStatus status;

    private Integer receivedQty;
    private Integer damagedQty;
    private String damageReason;
    private String notes;
}
