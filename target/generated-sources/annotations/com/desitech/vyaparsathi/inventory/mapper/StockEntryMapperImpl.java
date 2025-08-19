package com.desitech.vyaparsathi.inventory.mapper;

import com.desitech.vyaparsathi.catalog.entity.ItemVariant;
import com.desitech.vyaparsathi.inventory.dto.StockEntryDto;
import com.desitech.vyaparsathi.inventory.entity.StockEntry;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-19T13:17:59+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.16 (Eclipse Adoptium)"
)
@Component
public class StockEntryMapperImpl implements StockEntryMapper {

    @Override
    public StockEntryDto toDto(StockEntry entity) {
        if ( entity == null ) {
            return null;
        }

        StockEntryDto stockEntryDto = new StockEntryDto();

        stockEntryDto.setItemVariantId( entityItemVariantId( entity ) );
        stockEntryDto.setId( entity.getId() );
        stockEntryDto.setQuantity( entity.getQuantity() );
        stockEntryDto.setBatch( entity.getBatch() );
        stockEntryDto.setLastUpdated( entity.getLastUpdated() );

        return stockEntryDto;
    }

    @Override
    public StockEntry toEntity(StockEntryDto dto) {
        if ( dto == null ) {
            return null;
        }

        StockEntry stockEntry = new StockEntry();

        stockEntry.setItemVariant( stockEntryDtoToItemVariant( dto ) );
        stockEntry.setId( dto.getId() );
        stockEntry.setQuantity( dto.getQuantity() );
        stockEntry.setBatch( dto.getBatch() );
        stockEntry.setLastUpdated( dto.getLastUpdated() );

        return stockEntry;
    }

    private Long entityItemVariantId(StockEntry stockEntry) {
        if ( stockEntry == null ) {
            return null;
        }
        ItemVariant itemVariant = stockEntry.getItemVariant();
        if ( itemVariant == null ) {
            return null;
        }
        Long id = itemVariant.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    protected ItemVariant stockEntryDtoToItemVariant(StockEntryDto stockEntryDto) {
        if ( stockEntryDto == null ) {
            return null;
        }

        ItemVariant itemVariant = new ItemVariant();

        itemVariant.setId( stockEntryDto.getItemVariantId() );

        return itemVariant;
    }
}
