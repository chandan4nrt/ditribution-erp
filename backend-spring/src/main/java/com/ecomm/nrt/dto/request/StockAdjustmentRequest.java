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
public class StockAdjustmentRequest {

    @NotBlank(message = "Adjustment Number is required")
    private String adjustmentNumber;

    @NotNull(message = "Warehouse ID is required")
    private Long warehouseId;

    @NotNull(message = "Adjustment Date is required")
    private LocalDate adjustmentDate;

    private String remarks;

    @NotEmpty(message = "Items list cannot be empty")
    private List<AdjustmentItemRequest> items;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AdjustmentItemRequest {
        @NotNull(message = "Product ID is required")
        private Long productId;

        @NotNull(message = "Quantity change is required")
        private Integer changeQuantity;

        private String reason;
    }
}
