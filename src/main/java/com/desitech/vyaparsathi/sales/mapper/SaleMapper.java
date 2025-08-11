package com.desitech.vyaparsathi.sales.mapper;

import com.desitech.vyaparsathi.sales.dto.SaleDto;
import com.desitech.vyaparsathi.sales.entity.Sale;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SaleMapper {
    @Mapping(target = "items", source = "saleItems")
    SaleDto toDto(Sale sale);

    @Mapping(target = "saleItems", source = "items")
    Sale toEntity(SaleDto saleDto);

    List<SaleDto> toDtoList(List<Sale> sales);

}