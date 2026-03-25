package com.ecomm.nrt.controller;

import com.ecomm.nrt.dto.request.GRNRequest;
import com.ecomm.nrt.dto.response.GRNResponse;
import com.ecomm.nrt.service.GRNService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions/grns")
@RequiredArgsConstructor
@Tag(name = "Goods Received Note", description = "GRN and stock entry management")
public class GRNController {

    private final GRNService grnService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    @Operation(summary = "Create a new GRN and update stock levels")
    public ResponseEntity<GRNResponse> create(@Valid @RequestBody GRNRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(grnService.createGRN(request));
    }
}
