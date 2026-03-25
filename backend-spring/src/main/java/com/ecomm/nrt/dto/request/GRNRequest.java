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
public class GRNRequest {

    @NotBlank(message = "GRN Number is required")
    private String grnNumber;

    @NotNull(message = "Purchase Order ID is required")
    private Long purchaseOrderId;

    @NotNull(message = "Received Date is required")
    private LocalDate receivedDate;

    private String remarks;

    @NotEmpty(message = "Items list cannot be empty")
    private List<GRNItemRequest> items;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GRNItemRequest {
        @NotNull(message = "Product ID is required")
        private Long productId;

        @NotNull(message = "Received Quantity is required")
        private Integer quantity;

        private String remarks;
    }
}
