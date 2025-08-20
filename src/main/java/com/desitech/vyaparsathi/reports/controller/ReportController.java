package com.desitech.vyaparsathi.reports.controller;

import com.desitech.vyaparsathi.reports.dto.*;
import com.desitech.vyaparsathi.reports.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
@Tag(name = "Financial Reports", description = "Business reporting with corrected financial logic. Net revenue = sales - returns/discounts, Net profit = net revenue - COGS - operational expenses (excludes inventory purchases).")
public class ReportController {

    @Autowired
    private ReportService service;

    @GetMapping("/daily")
    @Operation(summary = "Get daily report", 
               description = "Returns daily financial report with corrected calculations. Net Profit = Sales - COGS - Operational Expenses. Outstanding Receivables = Sales - Payments.")
    public ResponseEntity<DailyReportDto> getDailyReport(
            @Parameter(description = "Report date in YYYY-MM-DD format", example = "2024-01-15")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(service.getDailyReport(date));
    }

    @GetMapping("/sales-summary")
    @Operation(
            summary = "Get sales summary for date range",
            description = "Returns comprehensive sales summary with COGS calculation and correct profit calculation. Net Profit excludes inventory purchases from expenses."
    )
    public ResponseEntity<SalesSummaryDto> getSalesSummary(
            @Parameter(description = "Start date in YYYY-MM-DD format", example = "2024-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String from,
            @Parameter(description = "End date in YYYY-MM-DD format", example = "2024-01-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String to) {

        LocalDate fromDate = (from == null || from.isBlank() || "undefined".equalsIgnoreCase(from))
                ? null
                : LocalDate.parse(from);

        LocalDate toDate = (to == null || to.isBlank() || "undefined".equalsIgnoreCase(to))
                ? null
                : LocalDate.parse(to);

        return ResponseEntity.ok(service.getSalesSummary(fromDate, toDate));
    }

    @GetMapping("/gst-summary")
    @Operation(summary = "Get GST summary for date range", description = "Returns GST summary with taxable value and GST amounts breakdown")
    public ResponseEntity<GstSummaryDto> getGstSummary(
            @Parameter(description = "Start date in YYYY-MM-DD format", example = "2024-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "End date in YYYY-MM-DD format", example = "2024-01-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(service.getGstSummary(from, to));
    }

    @GetMapping("/gst-breakdown")
    @Operation(summary = "Get GST breakdown by rate", description = "Returns GST breakdown grouped by GST rates (0%, 5%, 12%, 18%, 28%)")
    public ResponseEntity<List<GstBreakdownDto>> getGstSummaryByRate(
            @Parameter(description = "Start date in YYYY-MM-DD format", example = "2024-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "End date in YYYY-MM-DD format", example = "2024-01-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(service.getGstSummaryByRate(from, to));
    }

    @GetMapping("/items-sold")
    @Operation(summary = "Get all items sold", description = "Returns a list of all items sold with quantity, total sales, and last sold date.")
    public ResponseEntity<List<ItemsSoldDto>> getAllItemsSold(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String to
    ) {
        LocalDate fromDate = (from == null || from.isBlank() || "undefined".equalsIgnoreCase(from))
                ? null
                : LocalDate.parse(from);

        LocalDate toDate = (to == null || to.isBlank() || "undefined".equalsIgnoreCase(to))
                ? null
                : LocalDate.parse(to);

        return ResponseEntity.ok(service.getAllItemsSold(fromDate, toDate));
    }

    @GetMapping("/category-sales")
    @Operation(summary = "Get sales by category", description = "Returns sales totals grouped by item category.")
    public ResponseEntity<List<CategorySalesDto>> getCategorySales(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String to
    ) {
        LocalDate fromDate = (from == null || from.isBlank() || "undefined".equalsIgnoreCase(from))
                ? null
                : LocalDate.parse(from);

        LocalDate toDate = (to == null || to.isBlank() || "undefined".equalsIgnoreCase(to))
                ? null
                : LocalDate.parse(to);

        return ResponseEntity.ok(service.getCategorySales(fromDate, toDate));
    }

    @GetMapping("/customer-sales")
    @Operation(summary = "Get sales by customer", description = "Returns sales totals grouped by customer.")
    public ResponseEntity<List<CustomerSalesDto>> getCustomerSales(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String to
    ) {
        LocalDate fromDate = (from == null || from.isBlank() || "undefined".equalsIgnoreCase(from))
                ? null
                : LocalDate.parse(from);

        LocalDate toDate = (to == null || to.isBlank() || "undefined".equalsIgnoreCase(to))
                ? null
                : LocalDate.parse(to);

        return ResponseEntity.ok(service.getCustomerSales(fromDate, toDate));
    }

    @GetMapping("/expenses-summary")
    @Operation(summary = "Get expenses summary", description = "Returns total expenses for a date range.")
    public ResponseEntity<ExpensesSummaryDto> getExpensesSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String to
    ) {
        LocalDate fromDate = (from == null || from.isBlank() || "undefined".equalsIgnoreCase(from))
                ? null
                : LocalDate.parse(from);

        LocalDate toDate = (to == null || to.isBlank() || "undefined".equalsIgnoreCase(to))
                ? null
                : LocalDate.parse(to);

        return ResponseEntity.ok(service.getExpensesSummary(fromDate, toDate));
    }

    @GetMapping("/payments-summary")
    @Operation(summary = "Get payments summary", description = "Returns total payments collected for a date range.")
    public ResponseEntity<PaymentsSummaryDto> getPaymentsSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String to
    ) {
        LocalDate fromDate = (from == null || from.isBlank() || "undefined".equalsIgnoreCase(from))
                ? null
                : LocalDate.parse(from);

        LocalDate toDate = (to == null || to.isBlank() || "undefined".equalsIgnoreCase(to))
                ? null
                : LocalDate.parse(to);

        return ResponseEntity.ok(service.getPaymentsSummary(fromDate, toDate));
    }
}