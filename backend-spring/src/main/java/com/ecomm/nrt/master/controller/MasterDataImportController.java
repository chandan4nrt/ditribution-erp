package com.ecomm.nrt.master.controller;

import com.ecomm.nrt.dto.response.BulkUploadResponse;
import com.ecomm.nrt.master.service.ProductBulkUploadService;
import com.ecomm.nrt.master.service.SupplierBulkUploadService;
import com.ecomm.nrt.master.service.CustomerBulkUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/master/import")
@RequiredArgsConstructor
@Tag(name = "Master Data Import", description = "Bulk upload for products, customers, and suppliers")
public class MasterDataImportController {

    private final ProductBulkUploadService productBulkUploadService;
    private final SupplierBulkUploadService supplierBulkUploadService;
    private final CustomerBulkUploadService customerBulkUploadService;

    @PostMapping("/products")
    @PreAuthorize("hasAnyRole('ADMIN', 'PURCHASE_MANAGER')")
    @Operation(summary = "Import products via Excel file")
    public ResponseEntity<BulkUploadResponse> uploadProducts(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(productBulkUploadService.uploadProductMaster(file));
    }

    @PostMapping("/suppliers")
    @PreAuthorize("hasAnyRole('ADMIN', 'PURCHASE_MANAGER')")
    @Operation(summary = "Import suppliers via CSV file")
    public ResponseEntity<BulkUploadResponse> uploadSuppliers(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(supplierBulkUploadService.uploadSuppliers(file));
    }

    @PostMapping("/customers")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    @Operation(summary = "Import customers via CSV file")
    public ResponseEntity<BulkUploadResponse> uploadCustomers(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(customerBulkUploadService.uploadCustomers(file));
    }
}
