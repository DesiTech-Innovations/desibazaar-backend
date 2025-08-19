package com.desitech.vyaparsathi.inventory.mapper;

import com.desitech.vyaparsathi.inventory.dto.SupplierDto;
import com.desitech.vyaparsathi.inventory.entity.Supplier;
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
public class SupplierMapperImpl implements SupplierMapper {

    @Override
    public SupplierDto toDto(Supplier supplier) {
        if ( supplier == null ) {
            return null;
        }

        SupplierDto supplierDto = new SupplierDto();

        supplierDto.setId( supplier.getId() );
        supplierDto.setName( supplier.getName() );
        supplierDto.setContactPerson( supplier.getContactPerson() );
        supplierDto.setPhone( supplier.getPhone() );
        supplierDto.setEmail( supplier.getEmail() );
        supplierDto.setAddress( supplier.getAddress() );
        supplierDto.setGstin( supplier.getGstin() );

        return supplierDto;
    }

    @Override
    public Supplier toEntity(SupplierDto dto) {
        if ( dto == null ) {
            return null;
        }

        Supplier supplier = new Supplier();

        supplier.setId( dto.getId() );
        supplier.setName( dto.getName() );
        supplier.setContactPerson( dto.getContactPerson() );
        supplier.setPhone( dto.getPhone() );
        supplier.setEmail( dto.getEmail() );
        supplier.setAddress( dto.getAddress() );
        supplier.setGstin( dto.getGstin() );

        return supplier;
    }

    @Override
    public List<SupplierDto> toDtoList(List<Supplier> suppliers) {
        if ( suppliers == null ) {
            return null;
        }

        List<SupplierDto> list = new ArrayList<SupplierDto>( suppliers.size() );
        for ( Supplier supplier : suppliers ) {
            list.add( toDto( supplier ) );
        }

        return list;
    }
}
