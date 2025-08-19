package com.desitech.vyaparsathi.shop.mapper;

import com.desitech.vyaparsathi.shop.dto.ShopDto;
import com.desitech.vyaparsathi.shop.entity.Shop;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-19T13:17:59+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.16 (Eclipse Adoptium)"
)
@Component
public class ShopMapperImpl implements ShopMapper {

    @Override
    public Shop toEntity(ShopDto dto) {
        if ( dto == null ) {
            return null;
        }

        Shop shop = new Shop();

        shop.setName( dto.getName() );
        shop.setOwnerName( dto.getOwnerName() );
        shop.setAddress( dto.getAddress() );
        shop.setState( dto.getState() );
        shop.setGstin( dto.getGstin() );
        shop.setCode( dto.getCode() );
        shop.setLocale( dto.getLocale() );

        return shop;
    }

    @Override
    public ShopDto toDto(Shop entity) {
        if ( entity == null ) {
            return null;
        }

        ShopDto shopDto = new ShopDto();

        shopDto.setName( entity.getName() );
        shopDto.setOwnerName( entity.getOwnerName() );
        shopDto.setAddress( entity.getAddress() );
        shopDto.setState( entity.getState() );
        shopDto.setGstin( entity.getGstin() );
        shopDto.setCode( entity.getCode() );
        shopDto.setLocale( entity.getLocale() );

        return shopDto;
    }

    @Override
    public void updateShopFromDto(ShopDto dto, Shop shop) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getName() != null ) {
            shop.setName( dto.getName() );
        }
        if ( dto.getOwnerName() != null ) {
            shop.setOwnerName( dto.getOwnerName() );
        }
        if ( dto.getAddress() != null ) {
            shop.setAddress( dto.getAddress() );
        }
        if ( dto.getState() != null ) {
            shop.setState( dto.getState() );
        }
        if ( dto.getGstin() != null ) {
            shop.setGstin( dto.getGstin() );
        }
        if ( dto.getCode() != null ) {
            shop.setCode( dto.getCode() );
        }
        if ( dto.getLocale() != null ) {
            shop.setLocale( dto.getLocale() );
        }
    }
}
