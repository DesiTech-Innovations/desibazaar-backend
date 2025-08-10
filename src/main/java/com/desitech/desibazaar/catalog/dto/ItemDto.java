package com.desitech.desibazaar.catalog.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemDto {
    private Long id;
    private String sku;
    private String name;
    private String unit;
    private BigDecimal pricePerUnit;
    private String hsn;
    private int gstRate;
    private String photoPath;
}