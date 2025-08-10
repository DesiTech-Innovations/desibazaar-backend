package com.desitech.desibazaar.customer.entity;

import com.desitech.desibazaar.customer.entity.Customer;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "customer_ledger")
@Data
public class CustomerLedger {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private Double amount;
    private String type; // CREDIT or DEBIT
    private String description;

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters & Setters
}
