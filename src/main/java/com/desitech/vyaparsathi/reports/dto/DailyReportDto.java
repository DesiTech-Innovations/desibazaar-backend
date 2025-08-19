package com.desitech.vyaparsathi.reports.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DailyReportDto {
    private LocalDate date;
    private BigDecimal totalSales;
    private int numberOfSales;
    private BigDecimal totalExpenses;
    private BigDecimal totalPaid;
    private BigDecimal netRevenue;
    private BigDecimal netProfit;
    private BigDecimal outstandingReceivable;

}