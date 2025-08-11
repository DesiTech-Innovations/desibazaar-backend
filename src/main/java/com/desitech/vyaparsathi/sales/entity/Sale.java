package com.desitech.vyaparsathi.sales.entity;

import com.desitech.vyaparsathi.customer.entity.Customer;
import com.desitech.vyaparsathi.shop.entity.Shop;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
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

    private String paymentMethod;

    private boolean syncedFlag;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleItem> saleItems = new ArrayList<>();

    @PrePersist
    public void onCreate() {
        if (this.date == null) {
            this.date = LocalDateTime.now();
        }
    }
}