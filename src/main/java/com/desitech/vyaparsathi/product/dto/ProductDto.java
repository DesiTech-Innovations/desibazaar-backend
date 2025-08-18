package com.desitech.vyaparsathi.product.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDto {
    private Long itemVariantId;
    private String itemName;
    private String description;
    private String sku;
    private String color;
    private String size;
    private String design;
    private BigDecimal pricePerUnit;
    private BigDecimal availableQuantity;
    private String batch;
    private String photoPath;
}
