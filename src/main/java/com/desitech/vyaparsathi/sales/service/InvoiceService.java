package com.desitech.vyaparsathi.sales.service;

import com.desitech.vyaparsathi.payment.entity.Payment;
import com.desitech.vyaparsathi.payment.service.PaymentService;
import com.desitech.vyaparsathi.sales.entity.Sale;
import com.desitech.vyaparsathi.sales.entity.SaleItem;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

@Service
public class InvoiceService {

    @Autowired
    private PaymentService paymentService; // To fetch payment details

    public byte[] generatePdf(Sale sale) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            // Title
            Paragraph title = new Paragraph("GST Invoice", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Font.BOLD));
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
                BigDecimal totalPaid = paymentService.getPaymentsBySaleId(sale.getId()).stream()
                        .map(Payment::getAmountPaid)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal dueAmount = paymentService.calculateDueAmount(sale.getId(), sale.getTotalAmount());

                document.add(new Paragraph("Payment Details:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
                Table paymentTable = new Table(3);
                paymentTable.setWidth(80);
                paymentTable.setPadding(5);
                paymentTable.setSpacing(0);

                paymentTable.addCell(getHeaderCell("Method"));
                paymentTable.addCell(getHeaderCell("Amount"));
                paymentTable.addCell(getHeaderCell("Date"));

                paymentService.getPaymentsBySaleId(sale.getId()).forEach(payment -> {
                    paymentTable.addCell(payment.getMethod().name());
                    paymentTable.addCell(payment.getAmountPaid().toString());
                    paymentTable.addCell(payment.getPaymentDate().toString());
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
        } catch (DocumentException | IOException e) {
            throw new RuntimeException("PDF generation failed", e);
        }
    }

    private Cell getHeaderCell(String text) throws BadElementException {
        Cell cell = new Cell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Font.BOLD)));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBackgroundColor(new java.awt.Color(200, 200, 200));
        return cell;
    }
}