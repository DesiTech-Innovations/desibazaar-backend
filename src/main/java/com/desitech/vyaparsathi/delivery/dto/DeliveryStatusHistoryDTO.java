package com.desitech.vyaparsathi.delivery.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DeliveryStatusHistoryDTO {
    public Long id;
    public Long deliveryId;
    public String status; // e.g. "OUT_FOR_DELIVERY"
    public LocalDateTime changedAt;
    public String changedBy;
}