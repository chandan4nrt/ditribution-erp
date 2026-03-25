package com.ecomm.nrt.controller;

import com.ecomm.nrt.dto.request.StockTransferRequest;
import com.ecomm.nrt.dto.response.StockTransferResponse;
import com.ecomm.nrt.entity.TransferStatus;
import com.ecomm.nrt.service.StockTransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions/stock-transfers")
@RequiredArgsConstructor
@Tag(name = "Stock Transfer", description = "Moving goods between warehouses")
public class StockTransferController {

    private final StockTransferService stService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    @Operation(summary = "Create a new Stock Transfer between warehouses")
    public ResponseEntity<StockTransferResponse> create(@Valid @RequestBody StockTransferRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stService.createTransfer(request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    @Operation(summary = "Update transfer status (e.g., SENT -> RECEIVED affects stock in both locations)")
    public ResponseEntity<StockTransferResponse> updateStatus(
            @PathVariable Long id, @RequestParam TransferStatus status) {
        return ResponseEntity.ok(stService.updateStatus(id, status));
    }
}
