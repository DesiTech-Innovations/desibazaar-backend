package com.desitech.vyaparsathi.inventory.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PurchaseOrderDto {
    private Integer id;
    private String poNumber;
    private Integer supplierId;
    private LocalDateTime orderDate;
    private LocalDateTime expectedDeliveryDate;
    private BigDecimal totalAmount;
    private String status;
    private List<PurchaseOrderItemDto> items;
}