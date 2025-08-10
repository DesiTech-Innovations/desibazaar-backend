package com.desitech.desibazaar.inventory.mapper;

import com.desitech.desibazaar.inventory.dto.StockEntryDto;
import com.desitech.desibazaar.inventory.entity.StockEntry;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StockEntryMapper {
    StockEntry toEntity(StockEntryDto dto);
    StockEntryDto toDto(StockEntry entity);
}