package com.desitech.vyaparsathi.customer.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CustomerDto {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String gstNumber;
    private String panNumber;
    private String notes;
    private BigDecimal creditBalance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}