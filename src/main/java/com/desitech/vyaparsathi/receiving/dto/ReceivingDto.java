package com.desitech.vyaparsathi.receiving.dto;


import com.desitech.vyaparsathi.inventory.dto.SupplierDto;
import com.desitech.vyaparsathi.inventory.entity.Supplier;
import com.desitech.vyaparsathi.receiving.enums.ReceivingStatus;
import lombok.Data;
import com.desitech.vyaparsathi.receiving.dto.ReceivingItemDto;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReceivingDto {
    private Long id;
    private Long purchaseOrderId;
    private ReceivingStatus status;
    private LocalDateTime receivedAt;
    private String receivedBy;
    private String notes;
    private List<ReceivingItemDto> receivingItems;
    private Long shopId;
    private SupplierDto supplier;
}
