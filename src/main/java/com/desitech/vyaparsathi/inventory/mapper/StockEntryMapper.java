package com.desitech.vyaparsathi.inventory.mapper;

import com.desitech.vyaparsathi.inventory.dto.StockEntryDto;
import com.desitech.vyaparsathi.inventory.entity.StockEntry;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StockEntryMapper {
    StockEntry toEntity(StockEntryDto dto);
    StockEntryDto toDto(StockEntry entity);
}