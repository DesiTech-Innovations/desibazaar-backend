package com.desitech.vyaparsathi.inventory.mapper;

import com.desitech.vyaparsathi.inventory.dto.ItemDto;
import com.desitech.vyaparsathi.inventory.dto.ItemVariantDto;
import com.desitech.vyaparsathi.inventory.entity.Item;
import com.desitech.vyaparsathi.inventory.entity.ItemVariant;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.stream.Collectors;

/**
 * The CatalogMapper class is responsible for converting between entity objects
 * (Item, ItemVariant) and their corresponding DTOs (ItemDto, ItemVariantDto).
 * This class ensures that data is properly formatted for API communication
 * while keeping the business logic and database entities clean.
 */
@Component
public class ItemMapper {

    /**
     * Converts an Item entity to an ItemDto.
     * This method also maps the list of ItemVariants within the Item entity
     * to a list of ItemVariantDto in the DTO.
     *
     * @param item The Item entity to convert.
     * @return The resulting ItemDto.
     */
    public ItemDto toDto(Item item) {
        if (item == null) {
            return null;
        }

        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setCategory(item.getCategory());
        dto.setBrandName(item.getBrandName());

        // Map the list of variants if it exists
        if (item.getVariants() != null) {
            dto.setVariants(item.getVariants().stream()
                    .map(this::toDto)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    /**
     * Converts an ItemDto to an Item entity.
     * This method also maps the list of ItemVariantDto within the DTO
     * to a list of ItemVariant entities.
     *
     * @param dto The ItemDto to convert.
     * @return The resulting Item entity.
     */
    public Item toEntity(ItemDto dto) {
        if (dto == null) {
            return null;
        }

        Item item = new Item();
        item.setId(dto.getId());
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setCategory(dto.getCategory());
        item.setBrandName(dto.getBrandName());

        // Map the list of variant DTOs if it exists
        if (dto.getVariants() != null) {
            item.setVariants(dto.getVariants().stream()
                    .map(variantDto -> {
                        ItemVariant variant = toEntity(variantDto);
                        // Important: Link the variant back to its parent item
                        variant.setItem(item);
                        return variant;
                    })
                    .collect(Collectors.toList()));
        }

        return item;
    }

    /**
     * Converts a single ItemVariant entity to an ItemVariantDto.
     *
     * @param itemVariant The ItemVariant entity to convert.
     * @return The resulting ItemVariantDto.
     */
    public ItemVariantDto toDto(ItemVariant itemVariant) {
        if (itemVariant == null) {
            return null;
        }

        ItemVariantDto dto = new ItemVariantDto();
        dto.setId(itemVariant.getId());
        dto.setSku(itemVariant.getSku());
        dto.setUnit(itemVariant.getUnit());
        dto.setPricePerUnit(itemVariant.getPricePerUnit());
        dto.setHsn(itemVariant.getHsn());
        dto.setGstRate(itemVariant.getGstRate());
        dto.setPhotoPath(itemVariant.getPhotoPath());
        dto.setColor(itemVariant.getColor());
        dto.setSize(itemVariant.getSize());
        dto.setDesign(itemVariant.getDesign());
        dto.setLowStockThreshold(itemVariant.getLowStockThreshold());
        if (itemVariant.getItem() != null) {
            dto.setCategory(itemVariant.getItem().getCategory());
            dto.setItemName(itemVariant.getItem().getName());
            dto.setBrand(itemVariant.getItem().getBrandName());
            dto.setItemId(itemVariant.getItem().getId());
            dto.setDescription(itemVariant.getItem().getDescription());
        }
        dto.setCurrentStock(BigDecimal.ZERO);
        return dto;
    }

    /**
     * Converts a single ItemVariantDto to an ItemVariant entity.
     *
     * @param dto The ItemVariantDto to convert.
     * @return The resulting ItemVariant entity.
     */
    public ItemVariant toEntity(ItemVariantDto dto) {
        if (dto == null) {
            return null;
        }

    ItemVariant itemVariant = new ItemVariant();
    itemVariant.setId(dto.getId());
    itemVariant.setSku(dto.getSku());
    itemVariant.setUnit(dto.getUnit());
    itemVariant.setPricePerUnit(dto.getPricePerUnit());
    itemVariant.setHsn(dto.getHsn());
    itemVariant.setGstRate(dto.getGstRate());
    itemVariant.setPhotoPath(dto.getPhotoPath());
    itemVariant.setColor(dto.getColor());
    itemVariant.setSize(dto.getSize());
    itemVariant.setDesign(dto.getDesign());
    itemVariant.setLowStockThreshold(dto.getLowStockThreshold());

    return itemVariant;
    }
}
