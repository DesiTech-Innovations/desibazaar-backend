package com.desitech.vyaparsathi.customer.mapper;

import com.desitech.vyaparsathi.customer.dto.CustomerDto;
import com.desitech.vyaparsathi.customer.entity.Customer;
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
public class CustomerMapperImpl implements CustomerMapper {

    @Override
    public Customer toEntity(CustomerDto dto) {
        if ( dto == null ) {
            return null;
        }

        Customer customer = new Customer();

        customer.setId( dto.getId() );
        customer.setName( dto.getName() );
        customer.setPhone( dto.getPhone() );
        customer.setEmail( dto.getEmail() );
        customer.setAddressLine1( dto.getAddressLine1() );
        customer.setAddressLine2( dto.getAddressLine2() );
        customer.setCity( dto.getCity() );
        customer.setState( dto.getState() );
        customer.setPostalCode( dto.getPostalCode() );
        customer.setCountry( dto.getCountry() );
        customer.setGstNumber( dto.getGstNumber() );
        customer.setPanNumber( dto.getPanNumber() );
        customer.setNotes( dto.getNotes() );
        customer.setCreditBalance( dto.getCreditBalance() );
        customer.setCreatedAt( dto.getCreatedAt() );
        customer.setUpdatedAt( dto.getUpdatedAt() );

        return customer;
    }

    @Override
    public CustomerDto toDto(Customer entity) {
        if ( entity == null ) {
            return null;
        }

        CustomerDto customerDto = new CustomerDto();

        customerDto.setId( entity.getId() );
        customerDto.setName( entity.getName() );
        customerDto.setPhone( entity.getPhone() );
        customerDto.setEmail( entity.getEmail() );
        customerDto.setAddressLine1( entity.getAddressLine1() );
        customerDto.setAddressLine2( entity.getAddressLine2() );
        customerDto.setCity( entity.getCity() );
        customerDto.setState( entity.getState() );
        customerDto.setPostalCode( entity.getPostalCode() );
        customerDto.setCountry( entity.getCountry() );
        customerDto.setGstNumber( entity.getGstNumber() );
        customerDto.setPanNumber( entity.getPanNumber() );
        customerDto.setNotes( entity.getNotes() );
        customerDto.setCreditBalance( entity.getCreditBalance() );
        customerDto.setCreatedAt( entity.getCreatedAt() );
        customerDto.setUpdatedAt( entity.getUpdatedAt() );

        return customerDto;
    }

    @Override
    public List<CustomerDto> toDtoList(List<Customer> entities) {
        if ( entities == null ) {
            return null;
        }

        List<CustomerDto> list = new ArrayList<CustomerDto>( entities.size() );
        for ( Customer customer : entities ) {
            list.add( toDto( customer ) );
        }

        return list;
    }
}
