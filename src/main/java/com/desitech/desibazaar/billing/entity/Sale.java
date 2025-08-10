package com.desitech.desibazaar.billing.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String invoiceNo;
    private LocalDateTime date = LocalDateTime.now();
    private Long shopId;
    private Long customerId;
    private BigDecimal totalAmount;
    private BigDecimal roundOff;
    private String paymentMethod;
    private boolean syncedFlag;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL)
    private List<com.desitech.desibazaar.billing.entity.SaleItem> saleItems;
}