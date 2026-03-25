package com.ecomm.nrt.controller;

import com.ecomm.nrt.dto.request.StockAdjustmentRequest;
import com.ecomm.nrt.dto.response.StockAdjustmentResponse;
import com.ecomm.nrt.service.StockAdjustmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions/stock-adjustments")
@RequiredArgsConstructor
@Tag(name = "Stock Adjustment", description = "Manual stock correction and reconciliation")
public class StockAdjustmentController {

    private final StockAdjustmentService adjustmentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    @Operation(summary = "Create a new Stock Adjustment (Adds or Subtracts warehouse stock)")
    public ResponseEntity<StockAdjustmentResponse> create(@Valid @RequestBody StockAdjustmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adjustmentService.createAdjustment(request));
    }
}
