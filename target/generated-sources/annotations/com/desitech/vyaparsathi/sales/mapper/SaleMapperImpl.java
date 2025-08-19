package com.desitech.vyaparsathi.sales.mapper;

import com.desitech.vyaparsathi.sales.dto.SaleDto;
import com.desitech.vyaparsathi.sales.dto.SaleItemDto;
import com.desitech.vyaparsathi.sales.entity.Sale;
import com.desitech.vyaparsathi.sales.entity.SaleItem;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-19T13:17:59+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.16 (Eclipse Adoptium)"
)
@Component
public class SaleMapperImpl implements SaleMapper {

    @Override
    public SaleDto toDto(Sale sale) {
        if ( sale == null ) {
            return null;
        }

        SaleDto saleDto = new SaleDto();

        saleDto.setItems( saleItemListToSaleItemDtoList( sale.getSaleItems() ) );
        saleDto.setDate( sale.getDate() );
        saleDto.setRoundOff( sale.getRoundOff() );
        saleDto.setTotalAmount( sale.getTotalAmount() );

        return saleDto;
    }

    @Override
    public Sale toEntity(SaleDto saleDto) {
        if ( saleDto == null ) {
            return null;
        }

        Sale sale = new Sale();

        sale.setSaleItems( saleItemDtoListToSaleItemList( saleDto.getItems() ) );
        sale.setDate( saleDto.getDate() );
        sale.setTotalAmount( saleDto.getTotalAmount() );
        sale.setRoundOff( saleDto.getRoundOff() );

        return sale;
    }

    @Override
    public List<SaleDto> toDtoList(List<Sale> sales) {
        if ( sales == null ) {
            return null;
        }

        List<SaleDto> list = new ArrayList<SaleDto>( sales.size() );
        for ( Sale sale : sales ) {
            list.add( toDto( sale ) );
        }

        return list;
    }

    protected SaleItemDto saleItemToSaleItemDto(SaleItem saleItem) {
        if ( saleItem == null ) {
            return null;
        }

        SaleItemDto saleItemDto = new SaleItemDto();

        saleItemDto.setQty( saleItem.getQty() );
        saleItemDto.setUnitPrice( saleItem.getUnitPrice() );
        saleItemDto.setTaxableValue( saleItem.getTaxableValue() );

        return saleItemDto;
    }

    protected List<SaleItemDto> saleItemListToSaleItemDtoList(List<SaleItem> list) {
        if ( list == null ) {
            return null;
        }

        List<SaleItemDto> list1 = new ArrayList<SaleItemDto>( list.size() );
        for ( SaleItem saleItem : list ) {
            list1.add( saleItemToSaleItemDto( saleItem ) );
        }

        return list1;
    }

    protected SaleItem saleItemDtoToSaleItem(SaleItemDto saleItemDto) {
        if ( saleItemDto == null ) {
            return null;
        }

        SaleItem saleItem = new SaleItem();

        saleItem.setQty( saleItemDto.getQty() );
        saleItem.setUnitPrice( saleItemDto.getUnitPrice() );
        saleItem.setTaxableValue( saleItemDto.getTaxableValue() );

        return saleItem;
    }

    protected List<SaleItem> saleItemDtoListToSaleItemList(List<SaleItemDto> list) {
        if ( list == null ) {
            return null;
        }

        List<SaleItem> list1 = new ArrayList<SaleItem>( list.size() );
        for ( SaleItemDto saleItemDto : list ) {
            list1.add( saleItemDtoToSaleItem( saleItemDto ) );
        }

        return list1;
    }
}
