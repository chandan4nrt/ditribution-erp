package com.ecomm.nrt.controller;

import com.ecomm.nrt.dto.request.SalesInvoiceRequest;
import com.ecomm.nrt.dto.response.SalesInvoiceResponse;
import com.ecomm.nrt.service.SalesInvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions/sales-invoices")
@RequiredArgsConstructor
@Tag(name = "Sales Invoice", description = "Financial invoicing for sales orders")
public class SalesInvoiceController {

    private final SalesInvoiceService invoiceService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'ACCOUNTANT')")
    @Operation(summary = "Generate a Sales Invoice from an existing Sales Order")
    public ResponseEntity<SalesInvoiceResponse> create(@Valid @RequestBody SalesInvoiceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(invoiceService.createInvoice(request));
    }
}
