package com.ecomm.nrt.controller;

import com.ecomm.nrt.master.dto.ProductMasterResponse;
import com.ecomm.nrt.service.InventoryDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions/inventory-dashboard")
@RequiredArgsConstructor
@Tag(name = "Inventory Dashboard", description = "Real-time stock monitoring and low stock alerts")
public class InventoryDashboardController {

    private final InventoryDashboardService dashboardService;

    @GetMapping("/global")
    @Operation(summary = "Get aggregated stock levels for all products")
    public ResponseEntity<List<ProductMasterResponse>> getGlobalStock() {
        return ResponseEntity.ok(dashboardService.getGlobalInventory());
    }

    @GetMapping("/product/{productId}/breakdown")
    @Operation(summary = "Get stock levels per warehouse for a specific product")
    public ResponseEntity<Map<String, Integer>> getBreakdown(@PathVariable Long productId) {
        return ResponseEntity.ok(dashboardService.getProductStockBreakdown(productId));
    }

    @GetMapping("/alerts/low-stock")
    @Operation(summary = "Get list of products below their reorder levels")
    public ResponseEntity<List<ProductMasterResponse>> getAlerts() {
        return ResponseEntity.ok(dashboardService.getLowStockAlerts());
    }
}
