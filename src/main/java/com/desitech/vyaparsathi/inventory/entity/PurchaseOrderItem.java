package com.desitech.vyaparsathi.inventory.entity;

import com.desitech.vyaparsathi.catalog.entity.ItemVariant;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "purchase_order_item")
@Data
public class PurchaseOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_variant_id", nullable = false)
    private ItemVariant itemVariant;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "unit_cost", nullable = false)
    private BigDecimal unitCost;

}
