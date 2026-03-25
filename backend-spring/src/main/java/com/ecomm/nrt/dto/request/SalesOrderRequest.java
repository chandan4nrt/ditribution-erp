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
public class SalesOrderRequest {

    @NotBlank(message = "SO Number is required")
    private String soNumber;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Warehouse ID is required")
    private Long warehouseId;

    @NotNull(message = "SO Date is required")
    private LocalDate soDate;

    private String remarks;

    @NotEmpty(message = "Items list cannot be empty")
    private List<SOItemRequest> items;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SOItemRequest {
        @NotNull(message = "Product ID is required")
        private Long productId;

        @NotNull(message = "Quantity is required")
        private Integer quantity;

        @NotNull(message = "Unit Price is required")
        private java.math.BigDecimal unitPrice;
        
        private String remarks;
    }
}
