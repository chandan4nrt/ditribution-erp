package com.ecomm.nrt.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderRequest {

    @NotBlank(message = "PO Number is required")
    private String poNumber;

    @NotNull(message = "Supplier ID is required")
    private Long supplierId;

    @NotNull(message = "Warehouse ID is required")
    private Long warehouseId;

    @NotNull(message = "PO Date is required")
    private LocalDate poDate;

    private LocalDate expectedDeliveryDate;

    private String remarks;

    @NotEmpty(message = "Items list cannot be empty")
    private List<POItemRequest> items;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class POItemRequest {
        @NotNull(message = "Product ID is required")
        private Long productId;

        @NotNull(message = "Quantity is required")
        private Integer quantity;

        @NotNull(message = "Unit Price is required")
        private java.math.BigDecimal unitPrice;
        
        private String remarks;
    }
}
