package com.ecomm.nrt.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockAdjustmentResponse {
    private Long id;
    private String adjustmentNumber;
    private Long warehouseId;
    private String warehouseName;
    private LocalDate adjustmentDate;
    private String remarks;
    private List<AdjustmentItemResponse> items;
    private LocalDateTime createdAt;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AdjustmentItemResponse {
        private Long id;
        private Long productId;
        private String productName;
        private Integer changeQuantity;
        private String reason;
    }
}
