package com.desitech.vyaparsathi.inventory.mapper;

import com.desitech.vyaparsathi.inventory.dto.StockEntryDto;
import com.desitech.vyaparsathi.inventory.entity.StockEntry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StockEntryMapper {

    // Entity → DTO
    @Mapping(source = "itemVariant.id", target = "itemVariantId")
    StockEntryDto toDto(StockEntry entity);

    // DTO → Entity
    @Mapping(source = "itemVariantId", target = "itemVariant.id")
    StockEntry toEntity(StockEntryDto dto);
}
