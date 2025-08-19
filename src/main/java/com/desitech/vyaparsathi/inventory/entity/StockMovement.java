package com.desitech.vyaparsathi.inventory.entity;

import com.desitech.vyaparsathi.catalog.entity.ItemVariant;
import com.desitech.vyaparsathi.common.util.LocalDateTimeAttributeConverter;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_movement")
@Data
public class StockMovement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_variant_id", nullable = false)
    private ItemVariant itemVariant;

    @Column(name = "movement_type", nullable = false)
    private String movementType; // ADD, DEDUCT, ADJUST

    @Column(nullable = false)
    private BigDecimal quantity;

    @Column(name = "cost_per_unit")
    private BigDecimal costPerUnit;

    private String batch;
    private String reason;
    private String reference; // Reference to related transaction (e.g., Sale ID, Purchase Order ID)

    @Convert(converter = LocalDateTimeAttributeConverter.class)
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();
}