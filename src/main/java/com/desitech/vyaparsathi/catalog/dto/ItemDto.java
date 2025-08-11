package com.desitech.vyaparsathi.catalog.dto;

import lombok.Data;
import java.util.List;

@Data
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private List<ItemVariantDto> variants;
}
