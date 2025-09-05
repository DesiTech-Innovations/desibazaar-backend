package com.desitech.vyaparsathi.receiving.dto;

import com.desitech.vyaparsathi.inventory.dto.ItemDto;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreateReceivingDto {
    private Long purchaseOrderId;
    private LocalDate receivedDate;
    private List<ItemDto> items;
}
