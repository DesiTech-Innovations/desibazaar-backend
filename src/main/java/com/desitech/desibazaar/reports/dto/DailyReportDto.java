package com.desitech.desibazaar.reports.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DailyReportDto {
    private LocalDate date;
    private BigDecimal totalSales;
    private int numberOfSales;
    // Add more metrics as needed
}