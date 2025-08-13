package com.desitech.vyaparsathi.catalog.dto;

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
    private Long productId;
    private String color;
    private String size;
    private String design;
}
