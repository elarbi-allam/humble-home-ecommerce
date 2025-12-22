package com.elarbiallam.payment.controller;

import com.elarbiallam.payment.dto.PaymentRequest;
import com.elarbiallam.payment.model.Payment;
import com.elarbiallam.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService service;

    @PostMapping
    public ResponseEntity<Payment> createPayment(@RequestBody PaymentRequest request) {
        return ResponseEntity.ok(service.processPayment(request));
    }
}