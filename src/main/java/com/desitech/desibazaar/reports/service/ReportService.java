package com.desitech.desibazaar.reports.service;

import com.desitech.desibazaar.billing.entity.Sale;
import com.desitech.desibazaar.billing.entity.SaleItem;
import com.desitech.desibazaar.billing.repository.SaleRepository;
import com.desitech.desibazaar.reports.dto.DailyReportDto;
import com.desitech.desibazaar.reports.dto.GstSummaryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private SaleRepository saleRepository;

    public DailyReportDto getDailyReport(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        List<Sale> sales = saleRepository.findByDateBetween(start, end);

        BigDecimal totalSales = sales.stream()
                .map(Sale::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        DailyReportDto dto = new DailyReportDto();
        dto.setDate(date);
        dto.setTotalSales(totalSales);
        dto.setNumberOfSales(sales.size());
        return dto;
    }

    public GstSummaryDto getGstSummary(LocalDate from, LocalDate to) {
        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.plusDays(1).atStartOfDay();

        List<Sale> sales = saleRepository.findByDateBetween(start, end);

        BigDecimal taxable = BigDecimal.ZERO;
        BigDecimal cgst = BigDecimal.ZERO;
        BigDecimal sgst = BigDecimal.ZERO;
        BigDecimal igst = BigDecimal.ZERO;

        for (Sale sale : sales) {
            for (SaleItem item : sale.getSaleItems()) {
                taxable = taxable.add(item.getTaxableValue());
                cgst = cgst.add(item.getCgstAmt() != null ? item.getCgstAmt() : BigDecimal.ZERO);
                sgst = sgst.add(item.getSgstAmt() != null ? item.getSgstAmt() : BigDecimal.ZERO);
                igst = igst.add(item.getIgstAmt() != null ? item.getIgstAmt() : BigDecimal.ZERO);
            }
        }

        GstSummaryDto dto = new GstSummaryDto();
        dto.setTaxableValue(taxable);
        dto.setCgstTotal(cgst);
        dto.setSgstTotal(sgst);
        dto.setIgstTotal(igst);
        return dto;
    }
}