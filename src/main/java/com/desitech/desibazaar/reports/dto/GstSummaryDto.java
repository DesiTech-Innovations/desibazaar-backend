package com.desitech.desibazaar.reports.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GstSummaryDto {
    private BigDecimal taxableValue;
    private BigDecimal cgstTotal;
    private BigDecimal sgstTotal;
    private BigDecimal igstTotal;
    // Breakdown by rate or HSN if needed
}