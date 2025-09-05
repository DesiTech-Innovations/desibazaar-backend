package com.desitech.vyaparsathi.receiving.dto;

import com.desitech.vyaparsathi.receiving.enums.ReceivingItemStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReceivingItemDto {
    private Long id;
    private Long purchaseOrderItemId;
    private ReceivingItemStatus status;
    private Integer expectedQty;
    private Integer receivedQty;
    private Integer damagedQty;
    private String damageReason;
    private String notes;
    private String putAwayStatus;
}
