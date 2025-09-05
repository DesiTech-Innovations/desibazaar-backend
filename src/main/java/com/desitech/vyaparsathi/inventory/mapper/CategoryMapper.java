package com.desitech.vyaparsathi.inventory.mapper;

import com.desitech.vyaparsathi.inventory.dto.CategoryDto;
import com.desitech.vyaparsathi.inventory.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    /**
     * Converts a Category entity to a CategoryDto.
     * @param category The entity to convert.
     * @return The resulting DTO.
     */
    CategoryDto toDto(Category category);

    /**
     * Converts a CategoryDto to a Category entity.
     * The 'id' is ignored during this mapping to prevent issues when creating new entities.
     * @param categoryDto The DTO to convert.
     * @return The resulting entity.
     */
    @Mapping(target = "id", ignore = true)
    Category toEntity(CategoryDto categoryDto);
}