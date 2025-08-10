package com.desitech.desibazaar.billing.dto;

import lombok.Data;

import java.util.List;

@Data
public class SaleDto {
    private Long customerId;  // optional
    private List<SaleItemDto> items;
    private String paymentMethod;
}