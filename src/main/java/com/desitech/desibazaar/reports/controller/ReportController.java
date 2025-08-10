package com.desitech.desibazaar.reports.controller;

import com.desitech.desibazaar.reports.dto.DailyReportDto;
import com.desitech.desibazaar.reports.dto.GstSummaryDto;
import com.desitech.desibazaar.reports.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
public class ReportController {

    @Autowired
    private ReportService service;

    @GetMapping("/daily")
    public ResponseEntity<DailyReportDto> getDailyReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(service.getDailyReport(date));
    }

    @GetMapping("/gst-summary")
    public ResponseEntity<GstSummaryDto> getGstSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(service.getGstSummary(from, to));
    }
}