package com.desitech.desibazaar.catalog.mapper;

import com.desitech.desibazaar.catalog.dto.ItemDto;
import com.desitech.desibazaar.catalog.entity.Item;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    Item toEntity(ItemDto dto);
    ItemDto toDto(Item entity);
}