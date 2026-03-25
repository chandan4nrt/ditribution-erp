package com.ecomm.nrt.master.controller;

import com.ecomm.nrt.master.dto.ProductMasterRequest;
import com.ecomm.nrt.master.dto.ProductMasterResponse;
import com.ecomm.nrt.master.service.ProductMasterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/master/products")
@RequiredArgsConstructor
@Tag(name = "Product Master", description = "Product master data management")
public class ProductMasterController {

    private final ProductMasterService productMasterService;

    @GetMapping
    @Operation(summary = "List all active products (paginated, searchable by name/code/category)")
    public ResponseEntity<Page<ProductMasterResponse>> getAll(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(productMasterService.getAll(search, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<ProductMasterResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productMasterService.getById(id));
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Get products below reorder level")
    public ResponseEntity<List<ProductMasterResponse>> getLowStock() {
        return ResponseEntity.ok(productMasterService.getLowStockProducts());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PURCHASE_MANAGER')")
    @Operation(summary = "Create a new product")
    public ResponseEntity<ProductMasterResponse> create(@Valid @RequestBody ProductMasterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productMasterService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PURCHASE_MANAGER')")
    @Operation(summary = "Update a product")
    public ResponseEntity<ProductMasterResponse> update(
            @PathVariable Long id, @Valid @RequestBody ProductMasterRequest request) {
        return ResponseEntity.ok(productMasterService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate a product (soft delete)")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        productMasterService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
