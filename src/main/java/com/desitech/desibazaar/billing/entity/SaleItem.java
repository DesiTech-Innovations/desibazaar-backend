package com.desitech.desibazaar.billing.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
public class SaleItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sale_id")
    private Sale sale;

    private Long itemId;
    private BigDecimal qty;
    private BigDecimal unitPrice;
    private BigDecimal taxableValue;
    private int gstRate;
    private BigDecimal cgstAmt;
    private BigDecimal sgstAmt;
    private BigDecimal igstAmt;
}