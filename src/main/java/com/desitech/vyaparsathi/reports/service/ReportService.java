package com.desitech.vyaparsathi.reports.service;

import com.desitech.vyaparsathi.sales.entity.Sale;
import com.desitech.vyaparsathi.sales.entity.SaleItem;
import com.desitech.vyaparsathi.sales.repository.SaleRepository;
import com.desitech.vyaparsathi.expense.entity.Expense;
import com.desitech.vyaparsathi.expense.repository.ExpenseRepository;
import com.desitech.vyaparsathi.reports.dto.DailyReportDto;
import com.desitech.vyaparsathi.reports.dto.GstSummaryDto;
import com.desitech.vyaparsathi.reports.dto.GstBreakdownDto;
import com.desitech.vyaparsathi.reports.dto.SalesSummaryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    private List<Sale> getSalesByDateRange(LocalDate from, LocalDate to) {
        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.plusDays(1).atStartOfDay();
        return saleRepository.findByDateBetween(start, end);
    }

    private List<Expense> getExpensesByDateRange(LocalDate from, LocalDate to) {
        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.plusDays(1).atStartOfDay();
        return expenseRepository.findByDateBetweenAndDeletedFalse(start, end);
    }

    public DailyReportDto getDailyReport(LocalDate date) {
        List<Sale> sales = getSalesByDateRange(date, date);
        List<Expense> expenses = getExpensesByDateRange(date, date);

        BigDecimal totalSales = sales.stream()
                .map(Sale::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpenses = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        DailyReportDto dto = new DailyReportDto();
        dto.setDate(date);
        dto.setTotalSales(totalSales);
        dto.setNumberOfSales(sales.size());
        dto.setTotalExpenses(totalExpenses);
        return dto;
    }

    public SalesSummaryDto getSalesSummary(LocalDate from, LocalDate to) {
        List<Sale> sales = getSalesByDateRange(from, to);

        BigDecimal totalSales = sales.stream()
                .map(Sale::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalTaxableValue = sales.stream()
                .flatMap(s -> s.getSaleItems().stream())
                .map(SaleItem::getTaxableValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalGstAmount = sales.stream()
                .flatMap(s -> s.getSaleItems().stream())
                .map(si -> si.getCgstAmt().add(si.getSgstAmt()).add(si.getIgstAmt()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRoundOff = sales.stream()
                .map(Sale::getRoundOff)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        SalesSummaryDto dto = new SalesSummaryDto();
        dto.setFromDate(from);
        dto.setToDate(to);
        dto.setTotalSales(totalSales);
        dto.setTotalSalesCount(sales.size());
        dto.setTotalTaxableValue(totalTaxableValue);
        dto.setTotalGstAmount(totalGstAmount);
        dto.setTotalRoundOff(totalRoundOff);
        return dto;
    }

    public GstSummaryDto getGstSummary(LocalDate from, LocalDate to) {
        List<Sale> sales = getSalesByDateRange(from, to);

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
        dto.setTotalGst(cgst.add(sgst).add(igst));
        return dto;
    }

    public List<GstBreakdownDto> getGstSummaryByRate(LocalDate from, LocalDate to) {
        List<Sale> sales = getSalesByDateRange(from, to);

        Map<Integer, List<SaleItem>> itemsByGstRate = sales.stream()
                .flatMap(s -> s.getSaleItems().stream())
                .collect(Collectors.groupingBy(SaleItem::getGstRate));

        return itemsByGstRate.entrySet().stream()
                .map(entry -> {
                    int gstRate = entry.getKey();
                    List<SaleItem> items = entry.getValue();

                    BigDecimal taxable = items.stream().map(SaleItem::getTaxableValue).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal cgst = items.stream().map(SaleItem::getCgstAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal sgst = items.stream().map(SaleItem::getSgstAmt).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal igst = items.stream().map(SaleItem::getIgstAmt).reduce(BigDecimal.ZERO, BigDecimal::add);

                    GstBreakdownDto dto = new GstBreakdownDto();
                    dto.setGstRate(gstRate);
                    dto.setTaxableValue(taxable);
                    dto.setCgst(cgst);
                    dto.setSgst(sgst);
                    dto.setIgst(igst);
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
