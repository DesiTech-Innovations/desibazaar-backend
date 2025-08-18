package com.desitech.vyaparsathi.reports.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SalesSummaryDto {
    private LocalDate fromDate;
    private LocalDate toDate;
    private BigDecimal totalSales;
    private int totalSalesCount;
    private BigDecimal totalTaxableValue;
    private BigDecimal totalGstAmount;
    private BigDecimal totalRoundOff;
    private BigDecimal totalPaid;
    private BigDecimal netRevenue;
}