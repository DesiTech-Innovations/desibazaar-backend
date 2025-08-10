package com.desitech.desibazaar.shop.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String ownerName;
    private String address;
    private String state;
    private String gstin;
    private String code;
    private String locale;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}