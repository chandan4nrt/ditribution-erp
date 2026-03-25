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
public class StockTransferRequest {

    @NotBlank(message = "Transfer Number is required")
    private String transferNumber;

    @NotNull(message = "From Warehouse ID is required")
    private Long fromWarehouseId;

    @NotNull(message = "To Warehouse ID is required")
    private Long toWarehouseId;

    @NotNull(message = "Transfer Date is required")
    private LocalDate transferDate;

    private String remarks;

    @NotEmpty(message = "Items list cannot be empty")
    private List<TransferItemRequest> items;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TransferItemRequest {
        @NotNull(message = "Product ID is required")
        private Long productId;

        @NotNull(message = "Quantity is required")
        private Integer quantity;

        private String remarks;
    }
}
