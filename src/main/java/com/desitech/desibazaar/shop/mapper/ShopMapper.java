package com.desitech.desibazaar.shop.mapper;

import com.desitech.desibazaar.shop.dto.ShopDto;
import com.desitech.desibazaar.shop.entity.Shop;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ShopMapper {
    Shop toEntity(ShopDto dto);
    ShopDto toDto(Shop entity);
}