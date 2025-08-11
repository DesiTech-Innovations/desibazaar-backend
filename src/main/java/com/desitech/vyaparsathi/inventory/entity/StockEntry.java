package com.desitech.vyaparsathi.inventory.entity;

import com.desitech.vyaparsathi.catalog.entity.ItemVariant;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_entry")
@Data
public class StockEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Change from Long itemId to a ManyToOne relationship with ItemVariant
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private ItemVariant itemVariant;

    private BigDecimal quantity;

    private String batch;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated = LocalDateTime.now();
}