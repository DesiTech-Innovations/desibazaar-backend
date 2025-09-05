package com.desitech.vyaparsathi.reports.service;

import com.desitech.vyaparsathi.inventory.entity.Category;
import com.desitech.vyaparsathi.payment.dto.PaymentDto;
import com.desitech.vyaparsathi.payment.entity.Payment;
import com.desitech.vyaparsathi.payment.enums.PaymentSourceType;
import com.desitech.vyaparsathi.payment.service.PaymentService;
import com.desitech.vyaparsathi.reports.dto.*;
import com.desitech.vyaparsathi.sales.entity.Sale;
import com.desitech.vyaparsathi.sales.entity.SaleItem;
import com.desitech.vyaparsathi.sales.repository.SaleRepository;
import com.desitech.vyaparsathi.expense.entity.Expense;
import com.desitech.vyaparsathi.expense.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private PaymentService paymentService; // For payment data

    @Autowired
    private COGSCalculationService cogsCalculationService;

    private List<Sale> getSalesByDateRange(LocalDate from, LocalDate to) {
        if (from == null && to == null) {
            return saleRepository.findAll();
        }
        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.atTime(23, 59, 59); // End of day
        return saleRepository.findByDateBetween(start, end);
    }

    private List<Expense> getExpensesByDateRange(LocalDate from, LocalDate to) {
        if (from == null && to == null) {
            return expenseRepository.findAll();
        }
        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.atTime(23, 59, 59); // End of day
        return expenseRepository.findByDateBetweenAndDeletedFalse(start, end);
    }

    public DailyReportDto getDailyReport(LocalDate date) {
        List<Sale> sales = getSalesByDateRange(date, date);
        List<Expense> expenses = getExpensesByDateRange(date, date);

        BigDecimal totalSales = sales.stream()
                .map(Sale::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Only include operational expenses (inventory purchases should not be here)
        BigDecimal totalOperationalExpenses = expenses.stream()
                .map(Expense::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPaid = sales.stream()
                .filter(sale -> sale.getId() != null)
                .map(sale -> paymentService.getPaymentsBySource(PaymentSourceType.SALE, sale.getId()).stream()
                        .map(p -> p.getAmount() != null ? p.getAmount() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate COGS for sold items
        BigDecimal totalCOGS = cogsCalculationService.calculateCOGS(sales);

        DailyReportDto dto = new DailyReportDto();
        dto.setDate(date);
        dto.setTotalSales(totalSales);
        dto.setNumberOfSales(sales.size());
        dto.setTotalExpenses(totalOperationalExpenses);
        dto.setTotalPaid(totalPaid);
        dto.setTotalCOGS(totalCOGS);

        // Net Revenue = Total Sales (no returns/discounts deduction in current system)
        dto.setNetRevenue(totalSales);

        // Outstanding Receivables = Total Sales - Total Paid
        dto.setOutstandingReceivable(totalSales.subtract(totalPaid));

        // Net Profit = Total Sales - COGS - Operational Expenses
        dto.setNetProfit(totalSales.subtract(totalCOGS).subtract(totalOperationalExpenses));

        return dto;
    }

    public SalesSummaryDto getSalesSummary(LocalDate from, LocalDate to) {
        List<Sale> sales = getSalesByDateRange(from, to);
        List<Expense> expenses = getExpensesByDateRange(from, to);

        BigDecimal totalSales = sales.stream()
                .map(Sale::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalTaxableValue = sales.stream()
                .flatMap(s -> s.getSaleItems().stream())
                .map(SaleItem::getTaxableValue)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Only include operational expenses (inventory purchases should not be here due to validation)
        BigDecimal totalOperationalExpenses = expenses.stream()
                .map(Expense::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalGstAmount = sales.stream()
                .flatMap(s -> s.getSaleItems().stream())
                .map(si -> {
                    BigDecimal cgst = si.getCgstAmt() != null ? si.getCgstAmt() : BigDecimal.ZERO;
                    BigDecimal sgst = si.getSgstAmt() != null ? si.getSgstAmt() : BigDecimal.ZERO;
                    BigDecimal igst = si.getIgstAmt() != null ? si.getIgstAmt() : BigDecimal.ZERO;
                    return cgst.add(sgst).add(igst);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRoundOff = sales.stream()
                .map(Sale::getRoundOff)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPaid = sales.stream()
                .filter(sale -> sale.getId() != null)
                .map(sale -> paymentService.getPaymentsBySource(PaymentSourceType.SALE, sale.getId()).stream()
                        .map(p -> p.getAmount() != null ? p.getAmount() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate COGS for the period
        BigDecimal totalCOGS = cogsCalculationService.calculateCOGSForPeriod(sales, from, to);

        SalesSummaryDto dto = new SalesSummaryDto();
        dto.setFromDate(from);
        dto.setToDate(to);
        dto.setTotalSales(totalSales);
        dto.setTotalSalesCount(sales.size());
        dto.setTotalTaxableValue(totalTaxableValue);
        dto.setTotalGstAmount(totalGstAmount);
        dto.setTotalRoundOff(totalRoundOff);
        dto.setTotalPaid(totalPaid);
        dto.setTotalCOGS(totalCOGS);

        // Net Revenue = Total Sales (no returns/discounts in current system)
        dto.setNetRevenue(totalSales);

        // Outstanding Receivables = Total Sales - Total Paid
        dto.setOutstandingReceivable(totalSales.subtract(totalPaid));

        // Net Profit = Total Sales - COGS - Operational Expenses
        dto.setNetProfit(totalSales.subtract(totalCOGS).subtract(totalOperationalExpenses));

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
                taxable = taxable.add(item.getTaxableValue() != null ? item.getTaxableValue() : BigDecimal.ZERO);
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
                .collect(Collectors.groupingBy(si -> si.getGstType() != null ? si.getGstType().getRate() : 0));

        return itemsByGstRate.entrySet().stream()
                .map(entry -> {
                    int gstRate = entry.getKey();
                    List<SaleItem> items = entry.getValue();

                    BigDecimal taxable = items.stream().map(SaleItem::getTaxableValue).filter(v -> v != null).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal cgst = items.stream().map(SaleItem::getCgstAmt).filter(v -> v != null).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal sgst = items.stream().map(SaleItem::getSgstAmt).filter(v -> v != null).reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal igst = items.stream().map(SaleItem::getIgstAmt).filter(v -> v != null).reduce(BigDecimal.ZERO, BigDecimal::add);

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

    public List<ItemsSoldDto> getAllItemsSold(LocalDate fromDate, LocalDate toDate) {
        List<Sale> sales = getSalesByDateRange(fromDate, toDate);
        Map<Long, ItemsSoldDto> itemMap = new HashMap<>();
        for (Sale sale : sales) {
            for (SaleItem item : sale.getSaleItems()) {
                Long itemId = item.getItemVariant().getId();
                ItemsSoldDto dto = itemMap.getOrDefault(itemId, new ItemsSoldDto(
                        itemId,
                        item.getItemVariant().getItem().getName(),
                        item.getItemVariant().getSku(),
                        0,
                        BigDecimal.ZERO,
                        null
                ));
                dto.setTotalSold(dto.getTotalSold() + item.getQty().intValue());
                dto.setTotalSales(dto.getTotalSales().add(item.getUnitPrice().multiply(item.getQty())));
                LocalDate saleDate = sale.getDate().toLocalDate();
                if (dto.getLastSoldDate() == null || saleDate.isAfter(dto.getLastSoldDate())) {
                    dto.setLastSoldDate(saleDate);
                }
                itemMap.put(itemId, dto);
            }
        }
        return new ArrayList<>(itemMap.values());
    }

    public List<CategorySalesDto> getCategorySales(LocalDate fromDate, LocalDate toDate) {
        List<Sale> sales = getSalesByDateRange(fromDate, toDate);
        Map<String, CategorySalesDto> categoryMap = new HashMap<>();
        for (Sale sale : sales) {
            for (SaleItem item : sale.getSaleItems()) {
                Category category = item.getItemVariant().getItem().getCategory();
                CategorySalesDto dto = categoryMap.getOrDefault(category.getName(), new CategorySalesDto(
                        category.getName(),
                        0,
                        BigDecimal.ZERO
                ));
                dto.setTotalSold(dto.getTotalSold() + item.getQty().intValue());
                dto.setTotalSales(dto.getTotalSales().add(item.getUnitPrice().multiply(item.getQty())));
                categoryMap.put(category.getName(), dto);
            }
        }
        return new ArrayList<>(categoryMap.values());
    }

    public List<CustomerSalesDto> getCustomerSales(LocalDate fromDate, LocalDate toDate) {
        List<Sale> sales = getSalesByDateRange(fromDate, toDate);
        Map<Long, CustomerSalesDto> customerMap = new HashMap<>();
        for (Sale sale : sales) {
            if (sale.getCustomer() == null) continue;
            Long customerId = sale.getCustomer().getId();
            CustomerSalesDto dto = customerMap.getOrDefault(customerId, new CustomerSalesDto(
                    customerId,
                    sale.getCustomer().getName(),
                    BigDecimal.ZERO,
                    BigDecimal.ZERO
            ));
            dto.setTotalSales(dto.getTotalSales().add(sale.getTotalAmount()));
            // Outstanding = total - paid

            BigDecimal paid = paymentService.getPaymentsBySource(PaymentSourceType.SALE, sale.getId()).stream()
                    .map(p -> p.getAmount() != null ? p.getAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            dto.setTotalDue(dto.getTotalDue().add(sale.getTotalAmount().subtract(paid)));
            customerMap.put(customerId, dto);
        }
        return new ArrayList<>(customerMap.values());
    }

    public ExpensesSummaryDto getExpensesSummary(LocalDate fromDate, LocalDate toDate) {
        List<Expense> expenses = getExpensesByDateRange(fromDate, toDate);
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal operational = BigDecimal.ZERO;
        BigDecimal inventory = BigDecimal.ZERO;
        for (Expense e : expenses) {
            total = total.add(e.getAmount() != null ? e.getAmount() : BigDecimal.ZERO);
            if (e.getType() != null && e.getType().toLowerCase().contains("inventory")) {
                inventory = inventory.add(e.getAmount() != null ? e.getAmount() : BigDecimal.ZERO);
            } else {
                operational = operational.add(e.getAmount() != null ? e.getAmount() : BigDecimal.ZERO);
            }
        }
        return new ExpensesSummaryDto(total, operational, inventory);
    }

    public PaymentsSummaryDto getPaymentsSummary(LocalDate fromDate, LocalDate toDate) {
        List<Sale> sales = getSalesByDateRange(fromDate, toDate);
        BigDecimal totalPayments = BigDecimal.ZERO;
        int paymentCount = 0;

        for (Sale sale : sales) {
            List<PaymentDto> payments = paymentService.getPaymentsBySource(PaymentSourceType.SALE, sale.getId());
            totalPayments = totalPayments.add(
                    payments.stream()
                            .map(p -> p.getAmount() != null ? p.getAmount() : BigDecimal.ZERO)
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
            );
            paymentCount += payments.size();
        }
        return new PaymentsSummaryDto(totalPayments, paymentCount);
    }
}