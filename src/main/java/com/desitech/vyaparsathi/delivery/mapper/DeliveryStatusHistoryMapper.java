package com.desitech.vyaparsathi.delivery.mapper;

import com.desitech.vyaparsathi.delivery.dto.DeliveryStatusHistoryDTO;
import com.desitech.vyaparsathi.delivery.entity.DeliveryStatusHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DeliveryStatusHistoryMapper {
    @Mapping(source = "delivery.deliveryId", target = "deliveryId")
    DeliveryStatusHistoryDTO toDto(DeliveryStatusHistory entity);

    @Mapping(source = "deliveryId", target = "delivery.deliveryId")
    DeliveryStatusHistory toEntity(DeliveryStatusHistoryDTO dto);
}