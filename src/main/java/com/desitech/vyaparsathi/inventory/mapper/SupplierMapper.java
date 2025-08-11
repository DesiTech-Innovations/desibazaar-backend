package com.desitech.vyaparsathi.inventory.mapper;

import com.desitech.vyaparsathi.inventory.dto.SupplierDto;
import com.desitech.vyaparsathi.inventory.entity.Supplier;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SupplierMapper {
    SupplierDto toDto(Supplier supplier);
    Supplier toEntity(SupplierDto dto);
    List<SupplierDto> toDtoList(List<Supplier> suppliers);
}
