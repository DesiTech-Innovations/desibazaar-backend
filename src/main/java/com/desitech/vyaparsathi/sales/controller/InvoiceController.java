package com.desitech.vyaparsathi.sales.controller;

import com.desitech.vyaparsathi.sales.dto.InvoiceDto;
import com.desitech.vyaparsathi.sales.service.InvoiceService;
import com.desitech.vyaparsathi.sales.repository.SaleRepository;
import com.desitech.vyaparsathi.sales.entity.Sale;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;


    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadInvoicePdf(@RequestParam(required = false) Long saleId,
                                                    @RequestParam(required = false) String invoiceNo) {
        byte[] pdf = invoiceService.generatePdfBySaleIdOrInvoiceNo(saleId, invoiceNo);
        String filename = "invoice_" + (invoiceNo != null ? invoiceNo : (saleId != null ? saleId : "")) + ".pdf";
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .body(pdf);
    }
}
