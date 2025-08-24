package com.desitech.vyaparsathi.sales.service;

import com.desitech.vyaparsathi.common.exception.ExportAppException;
import com.desitech.vyaparsathi.payment.enums.PaymentSourceType;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desitech.vyaparsathi.payment.entity.Payment;
import com.desitech.vyaparsathi.payment.service.PaymentService;
import com.desitech.vyaparsathi.sales.entity.Sale;
import com.desitech.vyaparsathi.sales.entity.SaleItem;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.desitech.vyaparsathi.sales.repository.SaleRepository;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

@Service
public class InvoiceService {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceService.class);
    @Autowired
    private PaymentService paymentService; // To fetch payment details

    @Autowired
    private SaleRepository saleRepository;

    public byte[] generatePdf(Sale sale) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            // Title
            Paragraph title = new Paragraph("GST Invoice",
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Font.BOLD));
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            // Supplier Details
            document.add(new Paragraph("Supplier Details:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            document.add(new Paragraph("Name: " + sale.getShop().getName()));
            document.add(new Paragraph("Address: " + sale.getShop().getAddress()));
            document.add(new Paragraph("GSTIN: " + sale.getShop().getGstin()));
            document.add(Chunk.NEWLINE);

            // Recipient Details
            document.add(new Paragraph("Recipient Details:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            document.add(new Paragraph("Name: " + sale.getCustomer().getName()));
            document.add(new Paragraph("Address: " + sale.getCustomer().getAddressLine1()));
            if (sale.getCustomer().getGstNumber() != null && !sale.getCustomer().getGstNumber().isEmpty()) {
                document.add(new Paragraph("GSTIN/UIN: " + sale.getCustomer().getGstNumber()));
            }
            document.add(Chunk.NEWLINE);

            // Invoice Info
            document.add(new Paragraph("Invoice No: " + sale.getInvoiceNo()));
            document.add(new Paragraph("Date: " + sale.getDate().toString()));
            document.add(Chunk.NEWLINE);

            // Items Table
            Table itemTable = new Table(9);
            itemTable.setWidth(100);
            itemTable.setPadding(5);
            itemTable.setSpacing(0);

            // headers
            itemTable.addCell(getHeaderCell("Item"));
            itemTable.addCell(getHeaderCell("HSN Code"));
            itemTable.addCell(getHeaderCell("Qty"));
            itemTable.addCell(getHeaderCell("Price"));
            itemTable.addCell(getHeaderCell("Taxable Value"));
            itemTable.addCell(getHeaderCell("GST Rate"));
            itemTable.addCell(getHeaderCell("CGST"));
            itemTable.addCell(getHeaderCell("SGST"));
            itemTable.addCell(getHeaderCell("IGST"));

            for (SaleItem item : sale.getSaleItems()) {
                itemTable.addCell(item.getItemVariant().getItem().getName());
                itemTable.addCell(item.getItemVariant().getHsn());
                itemTable.addCell(item.getQty().toString());
                itemTable.addCell(item.getUnitPrice().toString());
                itemTable.addCell(item.getTaxableValue().toString());
                itemTable.addCell(item.getGstType().getRate() + "%");
                itemTable.addCell(item.getCgstAmt() != null ? item.getCgstAmt().toString() : "0.00");
                itemTable.addCell(item.getSgstAmt() != null ? item.getSgstAmt().toString() : "0.00");
                itemTable.addCell(item.getIgstAmt() != null ? item.getIgstAmt().toString() : "0.00");
            }

            document.add(itemTable);

            // Total
            document.add(new Paragraph("Total: " + sale.getTotalAmount(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            document.add(new Paragraph("Round Off: " + sale.getRoundOff()));
            document.add(Chunk.NEWLINE);

            // Payment Details
            if (sale.getId() != null) {
                var payments = paymentService.getPaymentsBySource(PaymentSourceType.SALE, sale.getId());
                BigDecimal totalPaid = payments.stream()
                        .map(p -> p.getAmount() != null ? p.getAmount() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal dueAmount = sale.getTotalAmount().subtract(totalPaid);

                document.add(new Paragraph("Payment Details:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
                Table paymentTable = new Table(3);
                paymentTable.setWidth(80);
                paymentTable.setPadding(5);
                paymentTable.setSpacing(0);

                // headers
                paymentTable.addCell(getHeaderCell("Method"));
                paymentTable.addCell(getHeaderCell("Amount"));
                paymentTable.addCell(getHeaderCell("Date"));

                payments.forEach(payment -> {
                    safeAddCell(paymentTable, payment.getPaymentMethod());
                    safeAddCell(paymentTable, payment.getAmount());
                    safeAddCell(paymentTable, payment.getPaymentDate());
                });

                document.add(paymentTable);
                document.add(new Paragraph("Total Paid: ₹" + totalPaid.toString()));
                if (dueAmount.compareTo(BigDecimal.ZERO) > 0) {
                    document.add(new Paragraph("Amount Due: ₹" + dueAmount.toString()));
                } else {
                    document.add(new Paragraph("Status: Paid"));
                }
                document.add(Chunk.NEWLINE);
            }

            // Signatory
            document.add(new Paragraph("_________________________"));
            document.add(new Paragraph("Authorized Signatory"));

            document.close();
            return baos.toByteArray();

        } catch (DocumentException | IOException | ExportAppException e) {
            logger.error("PDF generation failed for sale {}", sale != null ? sale.getId() : null, e);
            throw new ExportAppException("PDF generation failed", e);
        }
    }

    /**
     * Find Sale by saleId or invoiceNo and generate PDF. Throws if not found or neither param provided.
     */
    public byte[] generatePdfBySaleIdOrInvoiceNo(Long saleId, String invoiceNo) {
        Sale sale = null;
        if (saleId != null) {
            sale = saleRepository.findById(saleId).orElseThrow(() -> new RuntimeException("Sale not found"));
        } else if (invoiceNo != null) {
            sale = saleRepository.findByInvoiceNo(invoiceNo);
            if (sale == null) throw new RuntimeException("Sale not found for invoiceNo");
        } else {
            throw new IllegalArgumentException("saleId or invoiceNo required");
        }
        return generatePdf(sale);
    }

    private Cell getHeaderCell(String text) {
        try {
            Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Font.BOLD);
            Cell cell = new Cell(new Phrase(text, font));
            cell.setHeader(true);
            cell.setBackgroundColor(Color.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            return cell;
        } catch (Exception e) {
            logger.warn("Failed to create styled header cell for '{}'. Using fallback.", text, e);
            // Fallback plain cell (ensures PDF doesn't fail)
            return new Cell(text);
        }
    }

    private void safeAddCell(Table table, Object value) {
        String text = (value != null) ? value.toString() : "N/A";
        try {
            Cell cell = new Cell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA, 9)));
            cell.setVerticalAlignment(Cell.ALIGN_MIDDLE);
            table.addCell(cell);
        } catch (BadElementException e) {
            throw new RuntimeException("Failed to add cell to table", e);
        }
    }

}