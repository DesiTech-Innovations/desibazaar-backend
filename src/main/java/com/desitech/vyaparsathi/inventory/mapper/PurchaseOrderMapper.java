package com.desitech.vyaparsathi.inventory.mapper;

import com.desitech.vyaparsathi.inventory.dto.PurchaseOrderDto;
import com.desitech.vyaparsathi.inventory.dto.PurchaseOrderItemDto;
import com.desitech.vyaparsathi.inventory.entity.PurchaseOrder;
import com.desitech.vyaparsathi.inventory.entity.PurchaseOrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PurchaseOrderMapper {
    @Mapping(source = "supplier.id", target = "supplierId")
    PurchaseOrderDto toDto(PurchaseOrder purchaseOrder);

    @Mapping(source = "itemVariant.id", target = "itemVariantId")
    PurchaseOrderItemDto toDto(PurchaseOrderItem purchaseOrderItem);

    @Mapping(target = "supplier", ignore = true)
    PurchaseOrder toEntity(PurchaseOrderDto dto);

    @Mapping(target = "itemVariant", ignore = true)
    PurchaseOrderItem toEntity(PurchaseOrderItemDto dto);

    List<PurchaseOrderDto> toDtoList(List<PurchaseOrder> purchaseOrders);
}