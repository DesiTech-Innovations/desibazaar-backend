package com.desitech.vyaparsathi.inventory.entity;

import com.desitech.vyaparsathi.inventory.entity.ItemVariant;
import com.desitech.vyaparsathi.common.util.LocalDateTimeAttributeConverter;
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
    @JoinColumn(name = "item_variant_id", nullable = false)
    private ItemVariant itemVariant;

    private BigDecimal quantity;

    @Column(name = "cost_per_unit", nullable = false)
    private BigDecimal costPerUnit;

    private String batch;

    @Convert(converter = LocalDateTimeAttributeConverter.class)
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated = LocalDateTime.now();
}