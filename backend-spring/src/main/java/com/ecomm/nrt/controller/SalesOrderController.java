package com.ecomm.nrt.controller;

import com.ecomm.nrt.dto.request.SalesOrderRequest;
import com.ecomm.nrt.dto.response.SalesOrderResponse;
import com.ecomm.nrt.entity.SalesStatus;
import com.ecomm.nrt.service.SalesOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions/sales-orders")
@RequiredArgsConstructor
@Tag(name = "Sales Order", description = "Sales Order lifecycle and stock reservation management")
public class SalesOrderController {

    private final SalesOrderService soService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    @Operation(summary = "Create a new Sales Order (Checks stock availability)")
    public ResponseEntity<SalesOrderResponse> create(@Valid @RequestBody SalesOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(soService.createSO(request));
    }

    @GetMapping
    @Operation(summary = "Get all Sales Orders")
    public ResponseEntity<List<SalesOrderResponse>> getAll() {
        return ResponseEntity.ok(soService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Sales Order by ID")
    public ResponseEntity<SalesOrderResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(soService.getById(id));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    @Operation(summary = "Update Sales Order status (e.g., DRAFT -> OPEN will deduct stock)")
    public ResponseEntity<SalesOrderResponse> updateStatus(
            @PathVariable Long id, @RequestParam SalesStatus status) {
        return ResponseEntity.ok(soService.updateStatus(id, status));
    }
}
