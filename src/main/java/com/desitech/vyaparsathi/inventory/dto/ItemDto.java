package com.desitech.vyaparsathi.inventory.dto;

import lombok.Data;
import java.util.List;

@Data
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private String category;
    private String brandName;
    private List<ItemVariantDto> variants;
}
