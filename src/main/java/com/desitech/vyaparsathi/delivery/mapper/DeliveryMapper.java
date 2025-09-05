package com.desitech.vyaparsathi.delivery.mapper;

import com.desitech.vyaparsathi.delivery.dto.DeliveryDTO;
import com.desitech.vyaparsathi.delivery.entity.Delivery;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(
        componentModel = "spring",
        uses = {DeliveryPersonMapper.class, DeliveryStatusHistoryMapper.class}
)
public interface DeliveryMapper {
    @Mapping(source = "deliveryPerson", target = "deliveryPerson")
    @Mapping(source = "statusHistory", target = "statusHistory")
    DeliveryDTO toDto(Delivery entity);

    @Mapping(source = "deliveryPerson", target = "deliveryPerson")
    @Mapping(source = "statusHistory", target = "statusHistory")
    Delivery toEntity(DeliveryDTO dto);
}