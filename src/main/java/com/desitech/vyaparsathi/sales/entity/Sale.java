package com.desitech.vyaparsathi.sales.entity;

import com.desitech.vyaparsathi.common.util.LocalDateTimeAttributeConverter;
import com.desitech.vyaparsathi.customer.entity.Customer;
import com.desitech.vyaparsathi.payment.entity.Payment;
import com.desitech.vyaparsathi.shop.entity.Shop;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@ToString(exclude = {"payments", "saleItems", "customer", "shop"})
@EqualsAndHashCode(exclude = {"payments", "saleItems"})
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String invoiceNo;

    @Column(nullable = false)
    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;


    @Column(nullable = false)
    private BigDecimal totalAmount;

    private BigDecimal roundOff;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Payment> payments;

    private boolean syncedFlag;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<SaleItem> saleItems = new ArrayList<>();

    @PrePersist
    @Convert(converter = LocalDateTimeAttributeConverter.class)
    public void onCreate() {
        if (this.date == null) {
            this.date = LocalDateTime.now();
        }
    }
}