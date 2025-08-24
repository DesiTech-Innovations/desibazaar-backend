package com.desitech.vyaparsathi.receiving.dto;

import com.desitech.vyaparsathi.receiving.enums.ReceivingItemStatus;
import lombok.Data;

@Data
public class ReceivingItemDto {
    private Long id;
    private Long receivingId;
    private Long poItemId;
    private ReceivingItemStatus status;
    private Integer receivedQty;
    private Integer damagedQty;
    private String damageReason;
    private String notes;
}
