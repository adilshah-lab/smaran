package com.hinduprayerlock.backend.controller;

import com.hinduprayerlock.backend.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/invoice")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping("/{subscriptionId}")
    public ResponseEntity<byte[]> getInvoice(
            @PathVariable Long subscriptionId,
            Authentication authentication
    ) {

        String email = authentication.getName();

        byte[] pdf = invoiceService.generateInvoice(subscriptionId, email);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=invoice.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}