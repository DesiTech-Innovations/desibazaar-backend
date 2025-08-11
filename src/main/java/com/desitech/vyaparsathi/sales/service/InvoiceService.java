package com.desitech.vyaparsathi.sales.service;

import com.desitech.vyaparsathi.sales.entity.Sale;
import com.desitech.vyaparsathi.sales.entity.SaleItem;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class InvoiceService {

    public byte[] generatePdf(Sale sale) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            // Add title
            document.add(new Paragraph("GST Invoice", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
            document.add(new Paragraph("Invoice No: " + sale.getInvoiceNo()));
            document.add(new Paragraph("Date: " + sale.getDate().toString()));

            // Add table for items
            Table table = new Table(8);  // Columns: Item, Qty, Price, Taxable, GST%, CGST, SGST, IGST
            table.addCell("Item");
            table.addCell("Qty");
            table.addCell("Price");
            table.addCell("Taxable Value");
            table.addCell("GST Rate");
            table.addCell("CGST");
            table.addCell("SGST");
            table.addCell("IGST");

            for (SaleItem item : sale.getSaleItems()) {
                table.addCell(item.getItemVariant().getItem().getName());
                table.addCell(item.getQty().toString());
                table.addCell(item.getUnitPrice().toString());
                table.addCell(item.getTaxableValue().toString());
                table.addCell(item.getGstRate() + "%");
                table.addCell(item.getCgstAmt() != null ? item.getCgstAmt().toString() : "0");
                table.addCell(item.getSgstAmt() != null ? item.getSgstAmt().toString() : "0");
                table.addCell(item.getIgstAmt() != null ? item.getIgstAmt().toString() : "0");
            }

            document.add(table);

            // Totals
            document.add(new Paragraph("Total: " + sale.getTotalAmount()));
            document.add(new Paragraph("Round Off: " + sale.getRoundOff()));

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("PDF generation failed", e);
        }
    }
}