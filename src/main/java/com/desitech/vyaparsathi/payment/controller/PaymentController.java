package com.desitech.vyaparsathi.payment.controller;

import com.desitech.vyaparsathi.payment.dto.ApiResponse;
import com.desitech.vyaparsathi.payment.dto.PaymentReceivedRequest;
import com.desitech.vyaparsathi.payment.dto.PaymentResponse;
import com.desitech.vyaparsathi.payment.entity.Payment;
import com.desitech.vyaparsathi.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/record")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<ApiResponse<PaymentResponse>> recordPayment(
            @Valid @RequestBody PaymentReceivedRequest request) {

        Payment payment = paymentService.recordDuePayment(
                request.getSaleId(),
                request.getAmount(),
                request.getMethod()
        );

        PaymentResponse response = new PaymentResponse(
                payment.getId(),
                payment.getAmountPaid(),
                payment.getMethod().name(),
                payment.getStatus().name(),
                payment.getPaymentDate()
        );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        "Payment recorded successfully",
                        response,
                        payment.getId().toString()
                )
        );
    }


}
