
package com.desitech.vyaparsathi.audit.controller;
import com.desitech.vyaparsathi.common.exception.ApplicationException;
import com.desitech.vyaparsathi.common.exception.ExportAppException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desitech.vyaparsathi.audit.AuditLogDto;
import com.desitech.vyaparsathi.audit.export.AuditLogExportService;
import com.desitech.vyaparsathi.audit.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/audit")
@PreAuthorize("hasRole('OWNER')")
@Tag(name = "Audit Log", description = "Operations for viewing and searching audit logs")
public class AuditLogController {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogController.class);
    @Autowired
    private AuditLogService service;

    @GetMapping
    @Operation(summary = "Get audit logs within date range", description = "Retrieve all audit logs within specified date range")
    @ApiResponse(responseCode = "200", description = "Audit logs retrieved successfully")
    public ResponseEntity<List<AuditLogDto>> getLogs(
            @Parameter(description = "Start date for the logs") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date for the logs") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            List<AuditLogDto> result = service.getLogs(startDate, endDate);
            logger.info("Fetched audit logs from {} to {}", startDate, endDate);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error fetching audit logs from {} to {}: {}", startDate, endDate, e.getMessage(), e);
            throw new ApplicationException("Failed to fetch audit logs", e);
        }
    }

    @GetMapping("/user/{username}")
    @Operation(summary = "Get audit logs for user", description = "Retrieve all audit logs for a specific user")
    @ApiResponse(responseCode = "200", description = "Audit logs for user retrieved successfully")
    public ResponseEntity<List<AuditLogDto>> getLogsByUser(@PathVariable String username) {
        try {
            List<AuditLogDto> result = service.getLogsByUser(username);
            logger.info("Fetched audit logs for user={}", username);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error fetching audit logs for user={}: {}", username, e.getMessage(), e);
            throw new ApplicationException("Failed to fetch audit logs for user", e);
        }
    }
    @Autowired
    private AuditLogExportService exportService;
    @GetMapping("/export")
    @Operation(summary = "Export audit logs within date range", description = "Export all audit logs within specified date range as CSV, Excel, or PDF")
    @ApiResponse(responseCode = "200", description = "Audit logs exported successfully")
    public ResponseEntity<byte[]> exportLogs(
            @Parameter(description = "Start date for the logs") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date for the logs") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @Parameter(description = "Export format: csv, excel, pdf") @RequestParam(defaultValue = "csv") String format) {
        try {
            List<AuditLogDto> data = service.getLogs(startDate, endDate);
            byte[] file = exportService.exportAuditLogs(data, format);
            String contentType = "csv".equalsIgnoreCase(format) ? "text/csv" :
                    ("excel".equalsIgnoreCase(format) ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" :
                            ("pdf".equalsIgnoreCase(format) ? "application/pdf" : "application/octet-stream"));
            String fileName = "audit-logs-" + startDate.toLocalDate() + "-" + endDate.toLocalDate() + "." + ("csv".equalsIgnoreCase(format) ? "csv" : ("excel".equalsIgnoreCase(format) ? "xlsx" : ("pdf".equalsIgnoreCase(format) ? "pdf" : "dat")));
            logger.info("Exported audit logs as {} ({} bytes)", format, file.length);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=" + fileName)
                    .header("Content-Type", contentType)
                    .body(file);
        } catch (ExportAppException e) {
            logger.error("Failed to export audit logs: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during audit log export", e);
            throw new ApplicationException("Failed to export audit logs", e);
        }
    }

}
