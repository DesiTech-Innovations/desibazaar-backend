package com.desitech.vyaparsathi.purchaseorder.dto;

import com.desitech.vyaparsathi.common.util.CustomLocalDateTimeDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PurchaseOrderDto {
    private Integer id;
    private String poNumber;
    private Long supplierId;
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime orderDate;
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
    private LocalDateTime expectedDeliveryDate;
    private BigDecimal totalAmount;
    private String status;
    private String notes;
    private List<PurchaseOrderItemDto> items;
}
