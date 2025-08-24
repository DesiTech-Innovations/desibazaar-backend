
package com.desitech.vyaparsathi.audit.export;
import com.desitech.vyaparsathi.common.exception.ExportAppException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desitech.vyaparsathi.audit.AuditLogDto;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

@Service
public class AuditLogExportService {
    private static final Logger logger = LoggerFactory.getLogger(AuditLogExportService.class);
    public byte[] exportAuditLogs(List<AuditLogDto> data, String format) {
        if ("csv".equalsIgnoreCase(format)) {
            return exportCsv(data);
        } else if ("excel".equalsIgnoreCase(format)) {
            return exportExcel(data);
        } else if ("pdf".equalsIgnoreCase(format)) {
            return exportPdf(data);
        }
        throw new IllegalArgumentException("Unsupported export format: " + format);
    }

    private byte[] exportCsv(List<AuditLogDto> data) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(out);
             CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT
                     .withHeader("ID", "Username", "Action", "Entity", "Entity ID", "Details", "Timestamp"))) {
            for (AuditLogDto dto : data) {
                printer.printRecord(
                        dto.getId(),
                        dto.getUsername(),
                        dto.getAction(),
                        dto.getEntity(),
                        dto.getEntityId(),
                        dto.getDetails(),
                        dto.getTimestamp()
                );
            }
            printer.flush();
            return out.toByteArray();
        } catch (Exception e) {
            logger.error("Failed to export audit logs as CSV", e);
            throw new ExportAppException("Failed to export audit logs as CSV", e);
        }
    }

    private byte[] exportExcel(List<AuditLogDto> data) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Audit Logs");
            org.apache.poi.ss.usermodel.Row header = sheet.createRow(0);
            String[] columns = {"ID", "Username", "Action", "Entity", "Entity ID", "Details", "Timestamp"};
            for (int i = 0; i < columns.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
            }
            int rowIdx = 1;
            for (AuditLogDto dto : data) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(dto.getId());
                row.createCell(1).setCellValue(dto.getUsername());
                row.createCell(2).setCellValue(dto.getAction());
                row.createCell(3).setCellValue(dto.getEntity());
                row.createCell(4).setCellValue(dto.getEntityId());
                row.createCell(5).setCellValue(dto.getDetails());
                row.createCell(6).setCellValue(dto.getTimestamp() != null ? dto.getTimestamp().toString() : "");
            }
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            logger.error("Failed to export audit logs as Excel", e);
            throw new ExportAppException("Failed to export audit logs as Excel", e);
        }
    }

    private byte[] exportPdf(List<AuditLogDto> data) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();
            PdfPTable table = new PdfPTable(7);
            String[] columns = {"ID", "Username", "Action", "Entity", "Entity ID", "Details", "Timestamp"};
            for (String col : columns) {
                PdfPCell cell = new PdfPCell(new Phrase(col));
                cell.setBackgroundColor(com.lowagie.text.pdf.GrayColor.LIGHT_GRAY);
                table.addCell(cell);
            }
            for (AuditLogDto dto : data) {
                table.addCell(String.valueOf(dto.getId()));
                table.addCell(dto.getUsername());
                table.addCell(dto.getAction());
                table.addCell(dto.getEntity());
                table.addCell(dto.getEntityId());
                table.addCell(dto.getDetails());
                table.addCell(dto.getTimestamp() != null ? dto.getTimestamp().toString() : "");
            }
            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            logger.error("Failed to export audit logs as PDF", e);
            throw new ExportAppException("Failed to export audit logs as PDF", e);
        }
    }
}
