package com.desitech.desibazaar.inventory.entity;

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

    @Column(name = "item_id")
    private Long itemId;

    private BigDecimal quantity;

    private String batch;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated = LocalDateTime.now();
}