package com.desitech.vyaparsathi.reports.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class GstBreakdownDto {
    private int gstRate;
    private BigDecimal taxableValue;
    private BigDecimal cgst;
    private BigDecimal sgst;
    private BigDecimal igst;
}