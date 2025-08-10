package com.desitech.desibazaar.catalog.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sku;
    private String name;
    private String unit;  // e.g., "meter", "piece"
    private BigDecimal pricePerUnit;
    private String hsn;
    private int gstRate;
    private String photoPath;
}