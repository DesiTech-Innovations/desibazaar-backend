package com.desitech.vyaparsathi.customer.mapper;

import com.desitech.vyaparsathi.customer.dto.CustomerLedgerDto;
import com.desitech.vyaparsathi.customer.entity.Customer;
import com.desitech.vyaparsathi.customer.entity.CustomerLedger;
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
public class CustomerLedgerMapperImpl implements CustomerLedgerMapper {

    @Override
    public CustomerLedgerDto toDto(CustomerLedger entity) {
        if ( entity == null ) {
            return null;
        }

        CustomerLedgerDto customerLedgerDto = new CustomerLedgerDto();

        customerLedgerDto.setCustomerId( entityCustomerId( entity ) );
        customerLedgerDto.setId( entity.getId() );
        customerLedgerDto.setAmount( entity.getAmount() );
        customerLedgerDto.setType( entity.getType() );
        customerLedgerDto.setDescription( entity.getDescription() );
        customerLedgerDto.setCreatedAt( entity.getCreatedAt() );

        return customerLedgerDto;
    }

    @Override
    public CustomerLedger toEntity(CustomerLedgerDto dto) {
        if ( dto == null ) {
            return null;
        }

        CustomerLedger customerLedger = new CustomerLedger();

        customerLedger.setCustomer( customerLedgerDtoToCustomer( dto ) );
        customerLedger.setId( dto.getId() );
        customerLedger.setAmount( dto.getAmount() );
        customerLedger.setType( dto.getType() );
        customerLedger.setDescription( dto.getDescription() );
        customerLedger.setCreatedAt( dto.getCreatedAt() );

        return customerLedger;
    }

    @Override
    public List<CustomerLedgerDto> toDtoList(List<CustomerLedger> entities) {
        if ( entities == null ) {
            return null;
        }

        List<CustomerLedgerDto> list = new ArrayList<CustomerLedgerDto>( entities.size() );
        for ( CustomerLedger customerLedger : entities ) {
            list.add( toDto( customerLedger ) );
        }

        return list;
    }

    private Long entityCustomerId(CustomerLedger customerLedger) {
        if ( customerLedger == null ) {
            return null;
        }
        Customer customer = customerLedger.getCustomer();
        if ( customer == null ) {
            return null;
        }
        Long id = customer.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    protected Customer customerLedgerDtoToCustomer(CustomerLedgerDto customerLedgerDto) {
        if ( customerLedgerDto == null ) {
            return null;
        }

        Customer customer = new Customer();

        customer.setId( customerLedgerDto.getCustomerId() );

        return customer;
    }
}
