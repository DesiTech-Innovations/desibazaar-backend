package com.desitech.vyaparsathi.customer.mapper;

import com.desitech.vyaparsathi.customer.dto.CustomerLedgerDto;
import com.desitech.vyaparsathi.customer.entity.CustomerLedger;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerLedgerMapper {
    @Mapping(source = "customer.id", target = "customerId")
    CustomerLedgerDto toDto(CustomerLedger entity);

    @Mapping(source = "customerId", target = "customer.id")
    CustomerLedger toEntity(CustomerLedgerDto dto);

    List<CustomerLedgerDto> toDtoList(List<CustomerLedger> entities);
}