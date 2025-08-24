package com.desitech.vyaparsathi.inventory.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ItemVariantDto {
    private Long id;
    private String sku;
    private String unit;
    private BigDecimal pricePerUnit;
    private String hsn;
    private Integer gstRate;
    private String photoPath;
    private Long itemId;
    private String color;
    private String size;
    private String design;
    private BigDecimal currentStock;
    private String category;
    private String itemName;
    private BigDecimal lowStockThreshold;
    private String brand;
    private String description;
}
