package com.desitech.vyaparsathi.analytics.service;

import com.desitech.vyaparsathi.common.exception.ExportAppException;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.desitech.vyaparsathi.analytics.dto.ItemDemandPredictionDto;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVFormat;

@Service
public class AnalyticsExportService {
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsExportService.class);
    public byte[] exportItemDemand(List<ItemDemandPredictionDto> data, String format) {
        AnalyticsExportFormat fmt = AnalyticsExportFormat.fromString(format);
        return switch (fmt) {
            case EXCEL -> exportToExcel(data);
            case PDF -> exportToPdf(data);
            default -> exportToCsv(data);
        };
    }

    private byte[] exportToCsv(List<ItemDemandPredictionDto> data) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                     .withHeader("Item ID", "Item Name", "Predicted Demand", "Trend"))) {
            for (ItemDemandPredictionDto dto : data) {
                csvPrinter.printRecord(dto.getItemId(), dto.getItemName(), dto.getPredictedDemandNextMonth(), dto.getTrend());
            }
            csvPrinter.flush();
            return out.toByteArray();
        } catch (Exception e) {
            logger.error("Failed to export analytics as CSV", e);
            throw new ExportAppException("Failed to export analytics as CSV", e);
        }
    }

    private byte[] exportToExcel(List<ItemDemandPredictionDto> data) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Item Demand");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Item ID");
            header.createCell(1).setCellValue("Item Name");
            header.createCell(2).setCellValue("Predicted Demand");
            header.createCell(3).setCellValue("Trend");
            int rowIdx = 1;
            for (ItemDemandPredictionDto dto : data) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(dto.getItemId());
                row.createCell(1).setCellValue(dto.getItemName());
                row.createCell(2).setCellValue(dto.getPredictedDemandNextMonth());
                row.createCell(3).setCellValue(dto.getTrend());
            }
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            logger.error("Failed to export analytics as Excel", e);
            throw new ExportAppException("Failed to export analytics as Excel", e);
        }
    }

    private byte[] exportToPdf(List<ItemDemandPredictionDto> data) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();
            Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            document.add(new Paragraph("Item Demand Prediction", font));
            document.add(new Paragraph(" "));
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.addCell(new PdfPCell(new Phrase("Item ID")));
            table.addCell(new PdfPCell(new Phrase("Item Name")));
            table.addCell(new PdfPCell(new Phrase("Predicted Demand")));
            table.addCell(new PdfPCell(new Phrase("Trend")));
            for (ItemDemandPredictionDto dto : data) {
                table.addCell(String.valueOf(dto.getItemId()));
                table.addCell(dto.getItemName());
                table.addCell(String.valueOf(dto.getPredictedDemandNextMonth()));
                table.addCell(dto.getTrend());
            }
            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            logger.error("Failed to export analytics as PDF", e);
            throw new ExportAppException("Failed to export analytics as PDF", e);
        }
    }
}
