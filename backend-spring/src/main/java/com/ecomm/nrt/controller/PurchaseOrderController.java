package com.ecomm.nrt.controller;

import com.ecomm.nrt.dto.request.PurchaseOrderRequest;
import com.ecomm.nrt.dto.response.PurchaseOrderResponse;
import com.ecomm.nrt.service.PurchaseOrderService;
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
@RequestMapping("/api/transactions/purchase-orders")
@RequiredArgsConstructor
@Tag(name = "Purchase Order", description = "Purchase Order lifecycle management")
public class PurchaseOrderController {

    private final PurchaseOrderService poService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PURCHASE_MANAGER')")
    @Operation(summary = "Create a new Purchase Order")
    public ResponseEntity<PurchaseOrderResponse> create(@Valid @RequestBody PurchaseOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(poService.createPO(request));
    }

    @GetMapping
    @Operation(summary = "Get all Purchase Orders")
    public ResponseEntity<List<PurchaseOrderResponse>> getAll() {
        return ResponseEntity.ok(poService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Purchase Order by ID")
    public ResponseEntity<PurchaseOrderResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(poService.getById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PURCHASE_MANAGER')")
    @Operation(summary = "Delete a DRAFT Purchase Order")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        poService.deletePO(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'PURCHASE_MANAGER')")
    @Operation(summary = "Update PO status (DRAFT -> OPEN, etc.)")
    public ResponseEntity<PurchaseOrderResponse> updateStatus(
            @PathVariable Long id, @RequestParam com.ecomm.nrt.entity.PurchaseStatus status) {
        return ResponseEntity.ok(poService.updatePOStatus(id, status));
    }
}
