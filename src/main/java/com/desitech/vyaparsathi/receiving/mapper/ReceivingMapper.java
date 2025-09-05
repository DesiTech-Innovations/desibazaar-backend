package com.desitech.vyaparsathi.receiving.mapper;

import com.desitech.vyaparsathi.inventory.mapper.SupplierMapper;
import com.desitech.vyaparsathi.receiving.dto.ReceivingDto;
import com.desitech.vyaparsathi.receiving.entity.Receiving;
import com.desitech.vyaparsathi.receiving.dto.ReceivingItemDto;
import com.desitech.vyaparsathi.receiving.entity.ReceivingItem;
import com.desitech.vyaparsathi.receiving.enums.ReceivingItemStatus;
import com.desitech.vyaparsathi.receiving.enums.ReceivingStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReceivingMapper {

    private final SupplierMapper supplierMapper;

    public ReceivingMapper(SupplierMapper supplierMapper) {
        this.supplierMapper = supplierMapper;
    }

    public ReceivingDto toDTO(Receiving entity) {
        if (entity == null) return null;

        ReceivingDto dto = new ReceivingDto();
        dto.setId(entity.getId());
        dto.setPurchaseOrderId(entity.getPurchaseOrder() != null ? entity.getPurchaseOrder().getId() : null);
        dto.setStatus(entity.getStatus() != null ? entity.getStatus() : ReceivingStatus.DEFAULT);
        dto.setReceivedAt(entity.getReceivedAt());
        dto.setReceivedBy(entity.getReceivedBy());
        dto.setNotes(entity.getNotes());
        dto.setShopId(entity.getShop() != null ? entity.getShop().getId() : null);

        // Use supplierMapper here (via PO)
        if (entity.getPurchaseOrder() != null && entity.getPurchaseOrder().getSupplier() != null) {
            dto.setSupplier(supplierMapper.toDto(entity.getPurchaseOrder().getSupplier()));
        }

        if (entity.getItems() != null) {
            dto.setReceivingItems(
                    entity.getItems().stream()
                            .map(this::toDTO)
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }

    public ReceivingItemDto toDTO(ReceivingItem entity) {
        if (entity == null) return null;

        ReceivingItemDto dto = new ReceivingItemDto();
        dto.setId(entity.getId());
        dto.setPurchaseOrderItemId(entity.getPurchaseOrderItem() != null ? entity.getPurchaseOrderItem().getId() : null);
        dto.setStatus(entity.getStatus() != null ? entity.getStatus() : ReceivingItemStatus.DEFAULT);
        dto.setExpectedQty(entity.getExpectedQty());
        dto.setReceivedQty(entity.getReceivedQty());
        dto.setDamagedQty(entity.getDamagedQty());
        dto.setDamageReason(entity.getDamageReason());
        dto.setNotes(entity.getNotes());
        dto.setPutAwayStatus(entity.getPutAwayStatus());
        return dto;
    }

    public List<ReceivingDto> toDTOList(List<Receiving> entities) {
        if (entities == null) return null;
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}