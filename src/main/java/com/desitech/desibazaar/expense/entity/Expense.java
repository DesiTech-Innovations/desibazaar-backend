package com.desitech.desibazaar.expense.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "expense")
@Data
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shop_id")
    private Long shopId;

    private String type;

    private BigDecimal amount;

    private LocalDateTime date = LocalDateTime.now();

    private String notes;

    private boolean deleted = false;  // For soft-delete
}