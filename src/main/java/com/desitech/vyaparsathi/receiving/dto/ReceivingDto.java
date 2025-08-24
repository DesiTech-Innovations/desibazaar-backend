package com.desitech.vyaparsathi.receiving.dto;

import com.desitech.vyaparsathi.receiving.enums.ReceivingStatus;
import lombok.Data;
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
    private List<ReceivingItemDto> items;
}
